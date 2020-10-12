<?php
		
	
	$func = $_REQUEST["method"];
	$json = json_decode($_REQUEST["data"]);

	if(file_exists("init.php")) {
	    require "init.php";
	} else {
	    $resp = array("data" => "Arquivo init.php nao foi encontrado");
	    echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);	//Responde falha
	    exit;
	}

	if(file_exists("validatetoken.php")) {
	    require "validatetoken.php";
	} else {
	    $resp = array("data" => "Arquivo validatetoken.php nao foi encontrado");
	    echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);	//Responde falha
	    exit;
	}

	$entrega = new Entrega();
	
	if ($func === "cad_entrega"){

		$entrega->insEntrega($json);
	}
	else if ($func === "cancela"){

		$entrega->cancEntrega($json);
	}
	else{
        $resp = array("data" => "Nenhuma funчуo encontrada!");
		echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
	}		

	class Entrega 
	{
	    private $conn;

	    private function qry($sqlcmd){
	    	
	    	$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DB);

	    	$result = $conn->query($sqlcmd);

			$conn->close();
		
			$respArr = array();
			if ($result->num_rows > 0) {

				while($row = $result->fetch_array()){ // loop to store the data in an associative array.
				 //    $respArr[] = $row; 
					foreach($row as $key => $col){
		               $col_array[$key] = utf8_encode($col);
		            }
		            $respArr[] = $col_array;
				 }
				return $respArr;
			}
	    	return null;
	    }

	    // GetNewID funtion
	    private function getNewID($table){
			$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DB);
	    	$sql = "SELECT COALESCE(max(id)+1,1) id FROM $table";		
			return $conn->query($sql);
	    }

	    private function execQry($sqlcmd){	    	
	    	$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DB);
	    	$result = $conn->query($sqlcmd);
	    	$conn->close();
	    	return $result;
	    }

	    private function setCharsetUTF8(){
	    	$this->execQry("SET NAMES 'utf8', SET character_set_results = 'utf8', " . 
	    				   "character_set_client = 'utf8', character_set_connection = 'utf8', " .
	    				   "character_set_database = 'utf8', character_set_server = 'utf8'");			
	    }

	    public function getRegiaoBairroID($value){

			$sql = "SELECT id FROM regiao_bairro WHERE idbairro = $value";

	    	$chkres = $this->qry($sql);
	    	$id = -1;
	    	if ($result->num_rows > 0) {
	      		foreach($result as $row) {
			    	$id = $row['id'];
			    	break;
			 	}
	      	}

			return $id;
		}

		public function getRegiaoBairros($idregiao){
			$sql = "SELECT id FROM regiao_bairro WHERE idregiao = $idregiao";
			return $this->qry($sql);
		}

	    public function insEntrega($jsondata) {
	    	
			$token		= $jsondata->token;
            $id         = $jsondata->id;
            $peso       = $jsondata->peso;
            $pontos     = $jsondata->pontos;
            $obs		= $jsondata->obs;
            $residuos 	= json_decode($jsondata->residuos);
            $disp       = json_decode($jsondata->disponibilidades);
	      	$endereco 	= json_decode($jsondata->ender);

			$result = $this->getNewID('entrega');
            
            $newid = "";
            if ($result->num_rows > 0) {
                foreach($result as $row) {
                    $newid = $row['id'];
                    break;
                }
            }
            
			if ($newid > -1) {
                
                $cep = $endereco->cep;
		      	$logradouro = $endereco->logradouro;
		      	$nr = $endereco->nr;	      	
		      	$complemento = $endereco->complemento;
		      	$idcidade = $endereco->idcidade;
		      	$idbairro = $endereco->idbairro;
                $idtiporesid = $endereco->idtiporesid;
                $lat = $endereco->latitude;
                $lon = $endereco->longitude;
                
		      	$sql = "INSERT INTO entrega (id, identregador, dtcadastro, pesototal, pontos, observacao, " . 
		      		   "cep, logradouro, nr, complemento, idbairro, idtiporesidencia, lat, lon, status) " .
		      		   "VALUES ( $newid, $id, NOW(), $peso, $pontos, '$obs', " .
		      		   "'$cep', '$logradouro', '$nr', '$complemento', $idbairro, $idtiporesid, $lat, $lon, 0 )";
                
				if ( $this->execQry($sql) === FALSE )
  		    	{
	  				$resp = array("erro" => "6665" );
					echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);			//Responde falha
					exit();
  		    	}
                
		      	// ##########################   RESIDUOS   ################################		      	

		      	$sql = "INSERT INTO entrega_residuos ( id, identrega, idresiduo, peso, pontuacao ) VALUES";
				$sep = "";
				$sql_values = "";
                
                $result = $this->getNewID('entrega_residuos');                    
                $newidres = -1;
                if ($result->num_rows > 0) {
                    foreach($result as $row) {
                        $newidres = $row['id'];
                        break;
                    }
                }
				foreach($residuos as $rowres) {
                    
      				$sql_values .= $sep ." ( $newidres, $newid, $rowres->id, $rowres->peso, $rowres->peso_pontuacao )";		    
      				$sep = ", ";
                    $newidres++;
	      		}

	      		$sql = $sql . $sql_values;
                
	   		   	if ( $this->execQry($sql) === FALSE )
    		  	{
                    $this->execQry("DELETE FROM entrega WHERE id = $newid");
                    
	   		   		$resp = array("erro" => '6667');
					echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);		//Responde falha
					exit;
		       	}

				// ##########################   DISPONIBILIDATE   ################################

				if (isset($disp)) {

					$sql = "INSERT INTO entrega_disponibilidade ( id, identrega, data, hr_ini, hr_fim ) VALUES";
					$sep = "";
					$sql_values = "";
	                
	                $result = $this->getNewID('entrega_disponibilidade');
	                    
	                $newiddisp = -1;
	                if ($result->num_rows > 0) {
	                    foreach($result as $row) {
	                        $newiddisp = $row['id'];
	                        break;                    
	                    }
	                }    
	                    
					foreach($disp as $row) {
	                    $sql_values .= $sep ." ( $newiddisp, $newid, " .
	                    "STR_TO_DATE('$row->data', '%d/%m/%Y'), '$row->hrini', '$row->hrfim' )";                     
	                     $sep = ", ";
	                    $newiddisp++;
		      		}

		      		$sql = $sql . $sql_values;
	    			
		   		   	if ( $this->execQry($sql) === FALSE )
	    		  	{
	                    $this->execQry("DELETE FROM entrega WHERE id = $newid");
	                    $this->execQry("DELETE FROM entrega_residuos WHERE identrega = $newid");
		   		   		$resp = array("erro" => '6668');
						echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);		//Responde falha
						exit;
			       	}
		        }

    			$resp = array("data" => '100');
		    	echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);		//Responde sucesso
				

				// $resp = array("erro" =>$sql );
				// echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
				// exit();
				
			} 	// if newID

		}	// function - insEntrega

		// ##########################   canc_entrega   ################################		
		
		public function cancEntrega($jsondata) {
	    	
			$token		= $jsondata->token;
            $identrega  = $jsondata->identrega;
			                
	      	// ##########################   RESIDUOS   ################################		      	

	      	//$sql = "DELETE entrega_residuos WHERE identrega = $id";

		   	//    	if ( $this->execQry($sql) === FALSE )
		   	//  	{
			// 			$resp = array("erro" => "6668" );
			// 			echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);			//Responde falha
			// 			exit();
		   	//  	}

			// ##########################   DISPONIBILIDATE   ################################

			//$sql = "DELETE entrega_disponibilidade WHERE identrega = $id";

	   	   	//if ( $this->execQry($sql) === FALSE )
	   	 	//{
			//	$resp = array("erro" => "6667" );
			//	echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);			//Responde falha
			//	exit();
	   	 	//}

	    	//$sql = "DELETE entrega WHERE id = $id";

	    	$sql = "UPDATE entrega SET status = 9 WHERE id = $identrega";	    	

			if ( $this->execQry($sql) === FALSE )
	    	{
				$resp = array("erro" => "6665" );
				echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);			//Responde falha
				exit();
	    	}

	    	$resp = array("data" => '101');
		    echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);		//Responde sucesso
				
			
		} // function - cancEntrega

	}
?>