package org.ufam.gather4u.application;

public class Constants {
    public static final String NAME_DB = "gather_database";
    public static final int VERSION_DB = 4;
    public static final float MAP_ZOOM = 15;
    public static final String POSTS = "data";

    public static final int USER_PARTICIPANTE = 2;
    public static final int USER_COLETOR = 3;

    public enum FragmentType {
        Custom(0), MainSwapPendentes(1), MainSwapAceitas(2), MainSwapRealizadas(3);

        private final int valor;

        FragmentType (int valorOpcao){
            valor = valorOpcao;
        }

        public int getValor(){
            return valor;
        }
    }

    public enum MsgResult {
        OK(0), YES(1), NO(2), CANCEL(3);

        private final int valor;

        MsgResult(int valorOpcao){
            valor = valorOpcao;
        }

        public int getValor(){
            return valor;
        }
    }

    public enum ImageType {
        OK(0), INFO(1), ERROR(2),
        EXCLAMATION(3), QUESTION(4);

        private final int valor;

        ImageType(int valorOpcao){
            valor = valorOpcao;
        }

        public int getValor(){
            return valor;
        }
    }

    private static final String[] HttpMessages =
            { "getpais", "getestados", "getcidades",
              "getbairros", "getbairrosregiao", "getregioes",
              "getresiduos", "getregiaobairros", "getregioesout", "getuserresiduos",
              "getregiaouser", "gettotreguser","gettpresid",
              "getnovasentregas", "getaceitasentregas", "getfinalentregas",
              "getdispentrega", "getresiduoentrega", "pontos", "avaliacao", "avats" };

    public  enum HttpMessageType {
         UNDEFINED(-1), PAIS(0), ESTADOS(1), CIDADES(2), BAIRRO(3), BAIRROREGIAO(4), REGIAO(5),
         RESIDUOS(6), REGIAOBAIRRO(7), REGIAOOUT(8), USERRESIDUOS(9),
        USERREGIOES(10), USERREGIAOTOT(11), TIPORESID(12),
        ENTREGASNOVAS(13), ENTREGASACEITAS(14), ENTREGASFINALIZADAS(15),
        DISPENTREGAS(16), RESIDUOENTREGAS(17), PONTOS(18), AVALIACAO(19),
        AVATARS(20);

        private final int valor;

        HttpMessageType (int valorOpcao){
            valor = valorOpcao;
        }

        public int getValor(){
            return valor;
        }

        public String getString(){
            if (valor > -1) {
                return HttpMessages[valor];
            }
            return "";
        }
    }
}
