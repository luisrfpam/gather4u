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

		$resp = array("data" => "Arquivo init.php não foi encontrado");
	  	echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);	//Responde falha
		exit;
	}

	if(file_exists("validatetoken.php")) {
		require "validatetoken.php";
	} else {	

		$resp = array("data" => "Arquivo validatetoken.php não foi encontrado");
	  	echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);	//Responde falha
		exit;
	}

	$entrega = new Entrega();
	
	if ($func == "newparticipante"){		

		$user->insertUserParticipante($json);
	}
	else
	if ($func == "newcoletor"){	

		$user->insertUserColetor($json);
	}
	else{
		
		$resp = array("data" => "Nenhuma função encontrada!");
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
	    private function getNewID(){
            
			$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DB);
	    	$sql = "SELECT coalesce(max(id)+1,1) id FROM entrega";
		
			$result = $conn->query($sql);

			$newid = "";
	      	if ($result->num_rows > 0) {
	      		foreach($result as $row) {
			    	$newid = $row['id'];
			    	break;
			 	}
	      	}
			return $newid;	    	
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

	    public function insertUserParticipante($jsondata){

			$token	= $jsondata->token;

			$this->setCharsetUTF8();		
			
			$dados = json_decode($jsondata->user);

			$newid = $this->getNewID();
	      	$nome = $dados->nome;
	      	$login = $dados->login;
	      	$senha = $dados->senha;
	      	$dtcad = $dados->dtcad;
	      	$email = $dados->email;
	      	$whatsapp = $dados->whatsapp;
	      	$tipopessoa = $dados->tipopessoa;
	      	$cpf = $dados->cpf;
	      	$cep = $dados->cep;
	      	$logradouro = $dados->logradouro;
	      	$nr = $dados->nr;	      	
	      	$complemento = $dados->complemento;
	      	$idcidade = $dados->idcidade;
	      	$idbairro = $dados->idbairro;
	      	$avatar = $dados->avatar;
	      	$sexo = $dados->sexo;

	      	$idregbairro = $this->getRegiaoBairroID($idbairro);

	  		// $resp = array("data" => 'idregbairro: ' . $idregbairro);
			// echo json_encode($resp);
			// exit();

	      	$sql = "INSERT INTO usuario (id, nome, login, senha, dtcad, email, " . 
	      		   "whatsapp, tipopessoa, cpf, " .
	      		   "cep, logradouro, nr, complemento, idcidade, idregbairro, avatar, sexo, ativo) " .
	      		   "VALUES ( $newid, '$nome', '$login', '$senha', CAST('$dtcad' AS DATETIME ), '$email', " .
	      		   "'$whatsapp', $tipopessoa, '$cpf', " .
	      		   "'$cep', '$logradouro', '$nr', '$complemento', $idcidade, $idregbairro, '$avatar', $sexo, 1 )";

	      	if ( $this->qry($sql) === FALSE )
	      	{
	      		$resp = array("data" => 'Erro: na inclusão do usuário');
				echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);			//Responde falha
	      	}
	      	else
	      	{
	      		$idperfil = 2;						// Pessoa Física - Perfil 2 - Participante	      	

	      		$sql = "INSERT INTO usuario_perfil ( idusuario, idperfil ) VALUES ( $newid, $idperfil )";

		      	if ( $this->qry($sql) === FALSE )
	      		{
		      		$resp = array("data" => 'Erro: na inclusão do perfil');
					echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);		//Responde falha
		      	}
		      	else
		      	{
		      		$resp = array("data" => 'Cadastro realizado com sucesso.');
			  		echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);		//Responde sucesso
			  	}
	      	} 
	    } // function - insertUserParticipante

	    public function insertUserColetor($jsondata) {
	    	
			$token		= $jsondata->token;
	      	$dados 		= json_decode($jsondata->user);
	      	$regioes 	= json_decode($jsondata->regioes);
	      	$residuos 	= json_decode($jsondata->residuos);
	      	$bairros 	= json_decode($jsondata->bairros);
			
		 	// $resp = array("data" => var_export($regioes[0]->idregiao, true) );
			// echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
			// exit();

			$this->setCharsetUTF8();					

			$newid = $this->getNewID();				

			if ($newid > -1) {

				$nome = $dados->nome;
		      	$login = $dados->login;
		      	$senha = $dados->senha;
		      	$dtcad = $dados->dtcad;
		      	$email = $dados->email;
		      	$whatsapp = $dados->whatsapp;
		      	$tipoempresa = $dados->tipoempresa;
		      	$tipopessoa = $dados->tipopessoa;	     
		      	$cnpj = $dados->cnpj;
		      	$cep = $dados->cep;
		      	$logradouro = $dados->logradouro;
		      	$nr = $dados->nr;	      	
		      	$complemento = $dados->complemento;
		      	$cad_idcidade = $dados->idcidade;
		      	$cad_idbairro = $dados->idbairro;
		      	$avatar = $dados->avatar;

		      	$idregbairro = $this->getRegiaoBairroID($cad_idbairro);

		      	$sql = "INSERT INTO usuario (id, nome, login, senha, dtcad, email, " . 
		      		   "whatsapp, tipopessoa, tipoempresa, cnpj, " .
		      		   "cep, logradouro, nr, complemento, idcidade, idregbairro, avatar, ativo) " .
		      		   "VALUES ( $newid, '$nome', '$login', '$senha', CAST('$dtcad' AS DATETIME ), '$email', " .
		      		   "'$whatsapp', $tipopessoa, $tipoempresa, '$cnpj', " .
		      		   "'$cep', '$logradouro', '$nr', '$complemento', $cad_idcidade, $idregbairro, '$avatar', 1 )";

				if ( $this->execQry($sql) === FALSE )
  		    	{
	  				$resp = array("erro" => "Erro na inclusão do usuário" );
					echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);			//Responde falha
					exit();
  		    	}

  		    	//$newid = 23;

		      	// ##########################   PERFIL   ################################

				$idperfil = 3;	// Pessoa JurÃ­dica - Perfil 3 - Coletor	      		

    		  	$sql = "INSERT INTO usuario_perfil ( idusuario, idperfil ) VALUES ( $newid, $idperfil )";

	   		   	if ( $this->execQry($sql) === FALSE )
    		  	{
	   		   		$resp = array("erro" => "Erro na inclusão do perfil");
					echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);		//Responde falha
					exit;
		       	}

		      	// ##########################   RESÃ?DUOS   ################################		      	

		      	$sql = "INSERT INTO usuario_residuo ( idusuario, idresiduo ) VALUES";
				$sep = "";
				$sql_values = "";
				foreach($residuos as $row) {
      				$sql_values .= $sep ." ( $newid, $row->idresiduo )"; 				    
      				$sep = ", ";
	      		}

	      		$sql = $sql . $sql_values;
	      		
	   		   	if ( $this->execQry($sql) === FALSE )
    		  	{
	   		   		$resp = array("erro" => 'Erro na inclusão dos resíduos');
					echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);		//Responde falha
					exit;
		       	}

				// ##########################   REGIÃ•ES   ################################

				$sql = "INSERT INTO usuario_regiao_bairro ( idusuario, idregbairro ) VALUES";
				$sep = "";
				$sql_values = "";
				foreach($regioes as $row) {

					$regbairos = $this->getRegiaoBairros($row->idregiao);

					foreach($regbairos as $item) {

						$sql_values .= $sep ." ( $newid, $item[0] )"; 
      					$sep = ", ";
					}
	      		}

	      		$sql = $sql . $sql_values;
    			
	   		   	if ( $this->execQry($sql) === FALSE )
    		  	{
	   		   		$resp = array("erro" => 'Erro na inclusão das regiões');
					echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);		//Responde falha
					exit;
		       	}

				// ##########################   BAIRROS   ################################    			

				$sql = "INSERT INTO usuario_regiao_bairro ( idusuario, idregbairro ) VALUES";
				$sep = "";
				$sql_values = "";
				foreach($bairros as $row) {
      				$sql_values .= $sep ." ( $newid, $row->idoutrobairro )"; 
      				$sep = ", ";
      			}

      			$sql = $sql . $sql_values;
    			
	   		   	if ( $this->execQry($sql) === FALSE )
    		  	{
	   		   		$resp = array("erro" => 'Erro na inclusão dos bairros');
					echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);		//Responde falha
					exit;
		       	}

    			$resp = array("data" => 'Cadastro realizado com sucesso.');
		    	echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);		//Responde sucesso
				

				// $resp = array("erro" =>$sql );
				// echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
				// exit();
				
			} 	// if newID

		}	// function - insertUserColetor

	}	//
?>
