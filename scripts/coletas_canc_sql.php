<?php

	$func = $_REQUEST["method"];
	$json = json_decode($_REQUEST["data"]);

	if(file_exists("init.php")) 
	{
	    require "init.php";
	} 
	else 
	{
	    $resp = array("data" => "Arquivo init.php nao foi encontrado");
	    echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);	//Responde falha
	    exit;
	}

	if(file_exists("validatetoken.php")) 
	{
	    require "validatetoken.php";
	} 
	else 
	{
	    $resp = array("data" => "Arquivo validatetoken.php nao foi encontrado");
	    echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);	//Responde falha
	    exit;
	}

	$col = new Coleta();
	
	if ($func === "cancelar"){

		$col->cancColeta($json);
	}
	else
	{

        $resp = array("data" => "Nenhuma funcao encontrada!");
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

		public function cancColeta($jsondata) {
	    	
			$token		= $jsondata->token;
            $identrega  = $jsondata->identrega;
            $idcoleta  	= $jsondata->idcoleta;
            $motivo		= $jsondata->motivo;

	    	$sql = "UPDATE entrega SET status = 0 WHERE id = $identrega";	    	

			if ( $this->execQry($sql) === FALSE )
	    	{
				$resp = array("erro" => "6667" );
				echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);			//Responde falha
				exit();
	    	}

	    	$sql = "UPDATE coleta SET status = 9 WHERE id = $idcoleta";	    	

			if ( $this->execQry($sql) === FALSE )
	    	{
				$resp = array("erro" => "6668" );
				echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);			//Responde falha
				exit();
	    	}

	    	$result = $this->getNewID('coleta_canceladas');
            
            $newid = "";
            if ($result->num_rows > 0) {
                foreach($result as $row) {
                    $newid = $row['id'];
                    break;
                }
            }
            
			if ($newid > -1) {

		    	$sql = "INSERT INTO coleta_canceladas ( id, idcoleta, dtcanc, motivo ) " .
		    			"VALUES ( $newid, $idcoleta, NOW(), '$motivo' )";

				if ( $this->execQry($sql) === FALSE )
		    	{
					$resp = array("erro" => "6669" );
					echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);			//Responde falha
					exit();
		    	}
		    }

	    	$resp = array("data" => '101');
		    echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);		//Responde sucesso

		} // function - cancColeta

	}	// Classe Coleta
?>