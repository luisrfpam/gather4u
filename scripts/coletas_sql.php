<?php
	/*verifico se os dados estao vindos do formulario, porque se uma pessoa acessar essa pagina diretamente 
	poderia dar erro, entao eu testo antes*/
	//aqui ja expliquei, mas denovo: ele verifica se o arquivo existe
	//$meth = $_SERVER["REQUEST_METHOD"];
		
	$func = $_REQUEST["method"];
	$json = json_decode($_REQUEST["data"]);

	//aqui ja expliquei, mas denovo: ele verifica se o arquivo existe
	if(file_exists("init.php")) {
		require "init.php";
	} else {	

		$resp = array("data" => "Arquivo init.php nуo foi encontrado");
	  	echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);	//Responde falha
		exit;
	}

	if(file_exists("validatetoken.php")) {
		require "validatetoken.php";
	} else {	

		$resp = array("data" => "Arquivo validatetoken.php nуo foi encontrado");
	  	echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);	//Responde falha
		exit;
	}

	$coleta = new Coleta();
	
	if ($func === "cad_coleta"){

		$coleta->insColeta($json);
	}
	else if ($func === "finalizar"){

		$coleta->finColeta($json);
	}
	else if ($func === "avaliar"){

		$coleta->avalColeta($json);
	}
	else
	{
        $resp = array("data" => "Nenhuma funчуo encontrada!");
		echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
	}		

	class Coleta
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
	    
	    public function insColeta($jsondata) {
	    	
			$token		= $jsondata->token;
            $identrega  = $jsondata->identrega;
			$idcoletor  = $jsondata->idcoletor;			

			$result = $this->getNewID('coleta');
            
            $newid = "";
            if ($result->num_rows > 0) {
                foreach($result as $row) {
                    $newid = $row['id'];
                    break;
                }
            }
            
			if ($newid > -1) {
                
                $sql = "SELECT * FROM entrega WHERE id = $identrega";
            	$entrega = $this->qry($sql);

            	foreach($entrega as $row) {
                    
      				$logra 		= $row['logradouro'];
	            	$nr 		= $row['nr'];
	            	$compl 		= $row['complemento'];
	            	$lat 		= $row['lat'];
	            	$lon 		= $row['lon'];
					$idbairro 	= $row['idbairro'];

	      		}                

		      	$sql = "INSERT INTO coleta ( id, identrega, idcoletor, logradouro, nr, complemento, lat, lon, idbairro, status ) " .
		      		   "VALUES ( $newid, $identrega, $idcoletor, '$logra', '$nr', '$compl', $lat, $lon, $idbairro, 0 )";

		  		// $resp = array("data" => $sql );
				// echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);			//Responde falha
				// exit();
                
				if ( $this->execQry($sql) === FALSE )
  		    	{
	  				$resp = array("erro" => "6665" );
					echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);			//Responde falha
					exit();
  		    	}

				// ##########################   ENTREGA   ################################

				$sql = "UPDATE entrega SET status = 1 WHERE id = $identrega";				
    			
	   		   	if ( $this->execQry($sql) === FALSE )
    		  	{
	   		   		$resp = array("erro" => '6668');
					echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);		//Responde falha
					exit;
		       	}			

    			$resp = array("data" => '100');
		    	echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);		//Responde sucesso
				
			} 	// if newID

		}	// function - insColeta


		// ##########################   finColeta   ################################

		public function finColeta($jsondata) {
	    	
			$token		= $jsondata->token;
            $identrega  = $jsondata->identrega;
            $idcoleta  	= $jsondata->idcoleta;
            $peso       = $jsondata->peso;
            $pontos     = $jsondata->pontos;
            $residuos 	= json_decode($jsondata->residuos);

	    	$sql = "UPDATE entrega SET status = 2 WHERE id = $identrega";	    	

			if ( $this->execQry($sql) === FALSE )
	    	{
				$resp = array("erro" => "6665" );
				echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);			//Responde falha
				exit();
	    	}

	    	$sql = "UPDATE coleta SET status = 1, " .
	    		   " peso = $peso, " .
	    		   " pontos = $pontos " .
	    		   "WHERE id = $idcoleta";

			if ( $this->execQry($sql) === FALSE )
	    	{
				$resp = array("erro" => "6667" );
				echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);			//Responde falha
				exit();
	    	}

	    	// ##########################   RESIDUOS   ################################		      	

			$sql = "INSERT INTO coleta_residuos ( id, idcoleta, idresiduo, peso, pontuacao ) VALUES";
			$sep = "";
			$sql_values = "";
                
			$result = $this->getNewID('coleta_residuos');                    
			$newidres = -1;
			
			if ($result->num_rows > 0) {
				foreach($result as $row) {
				    $newidres = $row['id'];
				    break;
				}
			}
			
			foreach($residuos as $rowres) {
			    
					$sql_values .= $sep ." ( $newidres, $idcoleta, $rowres->id, $rowres->peso, $rowres->peso_pontuacao )";		    
					$sep = ", ";

			    $newidres++;
			}

			$sql = $sql . $sql_values;

		   	if ( $this->execQry($sql) === FALSE )
			{

				$resp = array("erro" => '6668');
				echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);		//Responde falha
				exit;
			}

			$resp = array("data" => '101');
		    echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);		//Responde sucesso
				
			
		} // function - finColeta


		// ##########################   avalColeta   ################################

		public function avalColeta($jsondata) {
	    	
			$token		= $jsondata->token;
            $identrega  = $jsondata->identrega;
            $idcoleta  	= $jsondata->idcoleta;
            $tipo     	= $jsondata->tipo;
            $nota       = $jsondata->nota;
            $descricao  = $jsondata->descricao;

            // ENTREGA
            if ($tipo === 1) {            	
	                
				$result = $this->getNewID('avaliacao_entrega');                    
				$idavalent = -1;
				
				if ($result->num_rows > 0) {
					foreach($result as $row) {
					    $idavalent = $row['id'];
					    break;
					}
				}

				$sql = "INSERT INTO avaliacao_entrega ( id, identrega, nota, descricao ) VALUES " . 
						"( $idavalent, $identrega, $nota, '$descricao' )";

				if ( $this->execQry($sql) === FALSE )
		    	{
					$resp = array("erro" => "6667" );
					echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);			//Responde falha
					exit();
		    	}

				$resp = array("data" => '100');
		    	echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);		//Responde sucesso
            	
            }
            else // COLETA
            if ($tipo === 2) {

            	$result = $this->getNewID('avaliacao_coleta');                    
				$idavalcol = -1;
				
				if ($result->num_rows > 0) {
					foreach($result as $row) {
					    $idavalcol = $row['id'];
					    break;
					}
				}

				$sql = "INSERT INTO avaliacao_coleta ( id, idcoleta, nota, descricao ) VALUES " . 
						"( $idavalcol, $idcoleta, $nota, '$descricao' )";

				if ( $this->execQry($sql) === FALSE )
		    	{
					$resp = array("erro" => "6668" );
					echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);			//Responde falha
					exit();
		    	}

				$resp = array("data" => '101');
		    	echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);		//Responde sucesso
            }				
			
		} // function - avalColeta



	}	//
?>