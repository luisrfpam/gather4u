<?php

	/*verifico se os dados estao vindos do formulario, porque se uma pessoa acessar essa pagina diretamente 
	poderia dar erro, entao eu testo antes*/	
	$func = $_REQUEST["method"];
	
	//aqui ja expliquei, mas denovo: ele verifica se o arquivo existe
	if(file_exists("init.php")) {
		require "init.php";
	} else {	

		$resp = array("data" => "Arquivo init.php naao foi encontrado");
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

	$loc = new DataLocation();  

	if ($func === "getpais") {

		$chkres = $loc->getDBPais();

		if (isset($chkres)) {

			$resp = array("data" => $chkres);
			echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES); 	//Responde sucesso
		}
		else
	 	{	
	 		$resp = array("data" => 'Erro: Erro na consulta de país');
		  	echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);	//Responde falha
	 	}
  	}
  	else if ($func === "getestados") {

  		$chkest = $loc->getDBEstados();

		if (isset($chkest)) {

			$resp = array("data" => $chkest);
			echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES); 	//Responde sucesso
		}
		else
	 	{	
	 		$resp = array("data" => 'Erro: Erro na consulta de estados');
		  	echo json_encode($resp);				//Responde falha
	 	}
  	}
  	else if ($func === "getcidades") {

  		$chkres = $loc->getDBCidades();

		if (isset($chkres)) {

			$resp = array("data" => $chkres);
			echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);	//Responde sucesso
		}
		else
	 	{	
	 		$resp = array("data" => 'Erro: Erro na consulta de cidades');
		  	echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);	//Responde falha
	 	}
  	}  	
  	else if ($func === "getbairros") {

  		$chkbairr = $loc->getDBBairros();

		if (isset($chkbairr)) {
			$resp = array("data" => $chkbairr);
			echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);	//Responde sucesso
		}
		else
	 	{	
	 		$resp = array("data" => 'Erro: Erro na consulta de bairros');
		  	echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);	//Responde falha
	 	}
  	}
  	else if ($func === "getbairrosregiao") {

  		$regs = $_REQUEST["regs"];

  		$chkbairr = $loc->getDBBairrosFromRegiao($regs);

		if (isset($chkbairr)) {

			$resp = array("data" => $chkbairr);
			echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES); 	//Responde sucesso
		}
		else
	 	{	
	 		$resp = array("data" => 'Erro: Erro na consulta de bairros regiões');
		  	echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);	//Responde falha
	 	}
  	}  	
  	else if ($func === "getregioes") {

  		$chkreg = $loc->getDBRegioes();

  		if (isset($chkreg)) {

  			$resp = array("data" => $chkreg);
  			echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES); 	//Responde sucesso
  		}
  		else
  	 	{	
  	 		$resp = array("data" => 'Erro: Erro na consulta de regiões');
  		  	echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);	//Responde falha
  	 	}
  	}
	  else if ($func === "getregioesout") {

  		$regs = $_REQUEST["regs"];

    		$chkout = $loc->getDBRegioesOut($regs);

  		if (isset($chkout)) {

  			$resp = array("data" => $chkout);
  			echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES); 	//Responde sucesso
  		}
  		else
  	 	{	
  	 		$resp = array("data" => 'Erro: Erro na consulta de regiões out');
  		  	echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);	//Responde falha
  	 	}
  	}
  	else if ($func === "getregiaobairros") {

  		$chkbairr = $loc->getDBRegiaoBairros();

  		if (isset($chkbairr)) {

  			$resp = array("data" => $chkbairr);
  			echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES); 	//Responde sucesso 
  		}
  		else
  	 	{	
  	 		$resp = array("data" => 'Erro: Erro na consulta de região bairros');
  		  	echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);	//Responde falha
  	 	}
  	}
  	else if ($func === "getresiduos") {

  		$chkreg = $loc->getDBResiduos();

  		if (isset($chkreg)) {

  			$resp = array("data" => $chkreg);
  			echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES); 	//Responde sucesso 
  		}
  		else
  	 	{	
  	 		$resp = array("data" => 'Erro: Erro na consulta de resíduos');
  		  	echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);	//Responde falha
  	 	}
  	}
    else if ($func === "getuserresiduos"){
        
        $iduser = $_REQUEST["id"];        
        $chkreg = $loc->getDBUserResiduos($iduser);

        if (isset($chkreg)) {

            $resp = array("data" => $chkreg);
            echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);     //Responde sucesso 
        }
        else
         {    
             $resp = array("erro" => '6663');
              echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);    //Responde falha
         }
    }
    else if ($func === "getregiaouser"){
        
       	$iduser = $_REQUEST["id"];        
        $chkreg = $loc->getDBUserRegioes($iduser);

        if (isset($chkreg)) {

            $resp = array("data" => $chkreg);
            echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);     //Responde sucesso 
        }
        else
         {    
             $resp = array("erro" => '6664');
              echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);    //Responde falha
         }
    }
    else if ($func === "gettotreguser"){
        
        $iduser = $_REQUEST["id"];        
        $chkreg = $loc->getDBUserRegTot($iduser);

        if (isset($chkreg)) {

            $resp = array("data" => $chkreg);
            echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);     //Responde sucesso 
        }
        else
         {    
             $resp = array("erro" => '6665');
              echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);    //Responde falha
         }
    }
    else if ($func === "gettpresid"){        
      
        $chkreg = $loc->getDBTpResid();

        if (isset($chkreg)) {

            $resp = array("data" => $chkreg);
            echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);     //Responde sucesso 
        }
        else
         {    
             $resp = array("erro" => '6667');
              echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);    //Responde falha
         }
    }    
    else if ($func === "getnovasentregas"){        
      
        $iduser = $_REQUEST["idusuario"];
        $identr = $_REQUEST["identregador"];
        
        $chkreg = $loc->getDBNovaEntrega($identr, $iduser);

        if (isset($chkreg)) {

            $resp = array("data" => $chkreg);
            echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);     //Responde sucesso 
        }
        else
         {    
             $resp = array("erro" => '6669');
              echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);    //Responde falha
         }
    }
    else if ($func === "getaceitasentregas"){        
      
        $iduser = $_REQUEST["idusuario"];
        $identr = $_REQUEST["identregador"];
        
        $chkreg = $loc->getDBAceitaEntrega($identr, $iduser);

        if (isset($chkreg)) {

            $resp = array("data" => $chkreg);
            echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);     //Responde sucesso 
        }
        else
         {    
             $resp = array("erro" => '66611');
              echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);    //Responde falha
         }
    }
    else if ($func === "getfinalentregas"){        
      
        $iduser = $_REQUEST["idusuario"];
        $identr = $_REQUEST["identregador"];
        
        $chkreg = $loc->getDBFinalEntrega($identr, $iduser);

        if (isset($chkreg)) {
            $resp = array("data" => $chkreg);
            echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);     //Responde sucesso 
        }
        else
        {   
            $resp = array("erro" => '66612');
            echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);    //Responde falha
        }
    }
    else if ($func === "getdispentrega"){        
      
        $identrega = $_REQUEST["identrega"];
        
        $chkreg = $loc->getDBDispEntrega($identrega);

        if (isset($chkreg)) {
            $resp = array("data" => $chkreg);
            echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);     //Responde sucesso 
        }
        else
        {   
            $resp = array("erro" => '66613');
            echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);    //Responde falha
        }
    }
    else if ($func === "getresiduoentrega"){        
      
        $identrega = $_REQUEST["identrega"];
        
        $chkreg = $loc->getDBResiduoEntrega($identrega);

        if (isset($chkreg)) {
            $resp = array("data" => $chkreg);
            echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);     //Responde sucesso 
        }
        else
        {   
            $resp = array("erro" => '66614');
            echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);    //Responde falha
        }
    }
    else if ($func === "getresiduocoleta"){        
      
        $idcoleta = $_REQUEST["idcoleta"];
        
        $chkreg = $loc->getDBResiduoColeta($idcoleta);

        if (isset($chkreg)) {
            $resp = array("data" => $chkreg);
            echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);     //Responde sucesso 
        }
        else
        {   
            $resp = array("erro" => '66615');
            echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);    //Responde falha
        }
    }
    else if ($func === "pontos"){
      
        $ident = $_REQUEST["id"];
        
        $chkreg = $loc->getDBPontos($ident);

        if (isset($chkreg)) {
            $resp = array("data" => $chkreg);
            echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);     //Responde sucesso 
        }
        else
        {   
            $resp = array("erro" => '66616');
            echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);    //Responde falha
        }
    }
    else if ($func === "avaliacao"){
      
        $idcol = $_REQUEST["id"];
        
        $chkreg = $loc->getDBAvaliacao($idcol);

        if (isset($chkreg)) {
            $resp = array("data" => $chkreg);
            echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);     //Responde sucesso 
        }
        else
        {   
            $resp = array("erro" => '66617');
            echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);    //Responde falha
        }
    }
    else if ($func === "avats"){
        
        $chkreg = $loc->getDBAvatars();

        if (isset($chkreg)) {
            $resp = array("data" => $chkreg);
            echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);     //Responde sucesso 
        }
        else
        {   
            $resp = array("erro" => '66618');
            echo json_encode($resp, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);    //Responde falha
        }
    }
       

/// DataLocation

  	class DataLocation {
		
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
	    	return null;				//Responde erro	    		    	
	    }

	    public function initCharset(){
	    	
	    	$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DB);
			$conn->set_charset('UTF8 ');

			// You have to execute the following statement *after* mysqli::set_charset()
			// in order to get the desired value for collation_connection:
			$conn->query("SET character_set_connection=utf8");
			$conn->query("SET character_set_client=utf8");
			$conn->query("SET character_set_results=utf8");
			$conn->close();
	    }	 
	    
	    public function getDBPais(){

	    	$sql = "SELECT * FROM pais";

	    	return $this->qry($sql);
		}

		public function getDBEstados(){

	    	$sql = "SELECT * FROM estado";

	    	return $this->qry($sql);
		}

		public function getDBCidades(){

	    	$sql = "SELECT * FROM cidade";

	    	return $this->qry($sql);
		}
            
		public function getDBBairros(){

	    	$sql = "SELECT id, bairro, idcidade FROM bairro";

	    	return $this->qry($sql);
		}
        
        public function getDBTpResid(){

            $sql = "SELECT id, descricao FROM tipo_residencia";

            return $this->qry($sql);
        }
        
        public function getDBRegioes(){

            $sql = "SELECT id, regiao FROM regiao";

            return $this->qry($sql);
        }

        public function getDBResiduos(){
            
            $sql = "SELECT id, residuo, descricao, cor, peso_pontuacao FROM residuos WHERE ativo = 1";
            return $this->qry($sql);
        }

		public function getDBBairrosFromRegiao($regioes){

			$params = implode(", ", $regioes);

	    	$sql = "SELECT rb.id, b.bairro FROM regiao_bairro rb, " .
	    		   "bairro b " .
	    		   "WHERE rb.idbairro = b.id and rb.idregiao in ( $params ) "; 
	    	return $this->qry($sql);
		}	

		public function getDBRegioesOut($regioes){	
			
			$params = implode(", ", $regioes);

	    	$sql = "SELECT id, regiao FROM regiao WHERE not id in ( $params )";			
	    	return $this->qry($sql);
		}

		public function getDBRegiaoBairros(){

	    	$sql = "SELECT id, idregiao, idbairro FROM regiao_bairro";

	    	return $this->qry($sql);
		}
        
        public function getDBUserResiduos($idusuario){            
            $sql = "SELECT idresiduo FROM usuario_residuo WHERE idusuario = '$idusuario'";
            return $this->qry($sql);
        }   
        
        public function getDBUserRegioes($idusuario){
            $sql = "SELECT rb.idregiao, idregbairro FROM usuario_regiao_bairro urb " .
                   "LEFT JOIN regiao_bairro rb on ( rb.id = urb.idregbairro ) " .
                   "WHERE urb.idusuario = '$idusuario'";
            return $this->qry($sql);
        }
        
        public function getDBUserRegTot($idusuario){            
            $sql = "SELECT rb.idregiao, COUNT(idregbairro) totuser, " .
                   "( select COUNT(r.id) FROM regiao_bairro r " .
                   "WHERE r.idregiao = rb.idregiao ) totreg " .
                   "FROM usuario_regiao_bairro " .
                   "LEFT JOIN regiao_bairro rb on " .
                   "( rb.id = usuario_regiao_bairro.idregbairro ) " .
                   "WHERE idusuario = '$idusuario' " .
                   "GROUP BY rb.idregiao ";
            return $this->qry($sql);
        }
        
        public function getDBNovaEntrega($ident, $idusu){ 

            if ($ident > -1) {
              $sql = "SELECT * FROM vw_entregas_pendentes WHERE identregador = $ident";
            }
            else
             $sql = "SELECT * FROM vw_entregas_pendentes WHERE idusuario = $idusu"; 

            
            return $this->qry($sql);
        }
        
        public function getDBAceitaEntrega($ident, $idusu){            
            if ($ident > -1) {
              $sql = "SELECT * FROM vw_entregas_aceitas WHERE identregador = $ident";
            }
            else
              $sql = "SELECT * FROM vw_entregas_aceitas WHERE idusuario = $idusu"; 

            return $this->qry($sql);
        }
        
        public function getDBFinalEntrega($ident, $idusu){            
            if ($idusu == -1) {
              $sql = "SELECT * FROM vw_entregas_finalizadas WHERE identregador = $ident";
            }
            else
              $sql = "SELECT * FROM vw_entregas_finalizadas WHERE idusuario = $idusu"; 
            return $this->qry($sql);
        }
        
        public function getDBDispEntrega($identrega){            
            $sql = "SELECT * FROM entrega_disponibilidade WHERE identrega = $identrega";
            return $this->qry($sql);
        }       
        
        public function getDBResiduoEntrega($identrega){
            $sql = "SELECT er.id, er.idresiduo, er.peso, er.peso * er.pontuacao pontos, r.residuo " .
                   "FROM entrega_residuos er " .
                   "LEFT JOIN residuos r ON ( r.id = er.idresiduo ) ";
            
            return $this->qry($sql);
        }

        public function getDBResiduoColeta($idcoleta){
            $sql = "SELECT cr.id, cr.idresiduo, cr.peso, cr.peso * cr.pontuacao pontos, r.residuo " .
                   "FROM coleta_residuos cr " .
                   "LEFT JOIN residuos r ON ( r.id = cr.idresiduo ) " .
                   "LEFT JOIN coleta c ON c.id = cr.idcoleta " .
                   "WHERE c.status != 9 " .
                   "AND cr.idcoleta = $idcoleta";
            
            return $this->qry($sql);
        }

        public function getDBPontos($identr){
            $sql = "SELECT * FROM vw_pontos_categ WHERE identregador = $identr";
            return $this->qry($sql);
        }


        public function getDBAvaliacao($idcol){
            $sql = "SELECT * FROM vw_avaliacao WHERE idcoletor = $idcol";
            return $this->qry($sql);
        }

        // getDBAvatars
        public function getDBAvatars(){
            $sql = "SELECT * FROM vw_avatars";
            return $this->qry($sql);
        }


        
	} /// DataLocation

	//// ----------------------------- END DataLocation    
?>
