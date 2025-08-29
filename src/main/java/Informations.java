public class Informations{

    public String resume;
    public String allInfos;
    public String avgPerSemester;
    public String expectedIra;
    public String lineWithInfos;
    public String personalInfos;

    public Informations(Main main){
        lineWithInfos = main.linesWithInfos();
        resume = main.resume();
        avgPerSemester = main.allAvgSemester();
        expectedIra = main.expectedIra();
        personalInfos = main.personalInfos();
    }

}
