import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args){
        Main m = new Main(args[0]);
        m.start();
    }

    List<Semester> semesters;
    Student student;

    Informations informations;

    public static final String GREEN = "\u001B[32m";
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";

    public Main(String content){
        init(content);
        informations = new Informations(this);
    }

    public void init(String content){

        content = content.replace("\t", " ");

        Pattern year = Pattern.compile("\\d{4}[.]\\d");
        Pattern code = Pattern.compile("[A-Z]{3}\\d{4}");
        Pattern name = Pattern.compile(" [A-Za-z].+\\(\\d{8}\\)");
        Pattern uce = Pattern.compile("UNIDADE CURRICULAR DE EXTENSÃO-UCE");
        Pattern number = Pattern.compile(" \\d+.+\\d+|-- 0 --");

        String[] lines = content.split("\n");

        String sName = regex("Aluno\\(a\\):\\s*(.*)", 1, content);
        String sCourse = regex("Curso:\\s*(.*)", 1, content);

        List<Semester> semesters = new ArrayList<>();

        boolean firstYear = true;
        boolean aux = false;

        for(String line : lines){

            if(line.contains("Voltar")) continue;

            Matcher m = year.matcher(line);

            if(m.find()){
                semesters.add(new Semester(m.group()));
                firstYear = false;
                aux = true;
                continue;
            }else if(firstYear){
                continue;
            }

            if(aux){
                aux = false;
                continue;
            }

            Semester last = semesters.get(semesters.size() - 1);

            String codeTemp = getGroup(code, line);
            String nameTemp = getGroup(name, line);

            if(nameTemp.isBlank()) nameTemp = getGroup(uce, line);

            List<Double> score = new ArrayList<>();
            int absence = 0;
            double avg = 0;

            m = number.matcher(line);
            String numberTemp;
            if(m.find()){

                numberTemp = m.group();

                if(numberTemp.contains("--")){
                    score = null;
                }else{

                    numberTemp = numberTemp.replace(",", ".");
                    String[] scores = numberTemp.split(" ");

                    for(int i = scores.length - 1; i >= 0; i--){

                        if(scores[i].isBlank()){
                            continue;
                        }

                        if(i == scores.length - 1){
                            absence = Integer.parseInt(scores[i]);
                            continue;
                        }

                        if(i == scores.length - 2){
                            avg = Double.parseDouble(scores[i]);
                            continue;
                        }

                        score.add(Double.parseDouble(scores[i]));

                    }

                }

            }

            Discipline d = new Discipline(codeTemp, nameTemp, score, absence, avg);

            last.disciplines.add(d);

        }

        student = new Student(sName, sCourse);
        this.semesters = semesters;
    }

    public String regex(String regex, int group, String line){

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(line);

        if(m.find()){
            return m.group(group);
        }else{
            return null;
        }

    }

    public String getGroup(Pattern p, String line){
        Matcher m = p.matcher(line);
        return m.find() ? m.group() : "";
    }

    public String expectedIra(){
        double sum = 0.0;
        int count = 0;
        for(var s : semesters){
            if(Double.isNaN(s.ira())) continue;
            sum += s.ira();
            count++;
        }
        return "IRA: " + String.format("%.1f", sum/count);
    }

    public String personalInfos(){

        Table t = new Table("personal information");

        for(var s : semesters){

            t.addR(s.date);

            t.addC("enrolled", s.totalEnrolled().toString());
            t.addC("absences", s.totalAbsences().toString());
            t.addC("avg", s.ira().toString());

        }

        return t.table();
    }

    public String resume(){

        Table t = new Table("Infomations");

        for(var s : semesters){

            for(var d : s.disciplines){

                int i = 1;

                if(shorten(d.name).isBlank()) continue;

                t.addR(shorten(d.name));

                if(d.scores != null){
                    for(var scr : d.scores){
                        t.addC("Un." + i++, scr.toString());
                    }

                    while(i != 4){
                        t.addC("Un." + i++, "-");
                    }

                    t.addC("total", d.average().toString());
                }else{
                    while(i != 4){
                    t.addC("Un." + i++, "-");
                    }
                    t.addC("total", "-");
                }

            }

        }

        return t.table();
    }

//    public String resume(){
//
//        List<String> resume = new ArrayList<>(Arrays.stream(resumeAll().split("\n\n")).toList());
//        resume.remove(resume.size() - 1);
//        StringBuilder sb = new StringBuilder();
//
//        int size = maxBorder(resume);
//
//        for(var s : resume){
//
//            String[] split = s.split("\n");
//
//            for(var line : split){
//                sb.append(normalizeLine(line, size)).append("\n");
//            }
//
//
//        }
//
//        return sb.toString();
//
//    }

    private String shorten(String str){

        List<String> fields = new ArrayList<>(Arrays.stream(str.split(" ")).filter(i -> !i.isBlank()).toList());

        if(fields.contains("EXTENSÃO-UCE")) return "UCE ";

        if (fields.isEmpty()) return "";

        fields.remove(fields.size() - 1);

        if(fields.size() == 1) return fields.get(0) + " ";

//        int hiden = fields.size();
//        StringBuilder s = new StringBuilder();
//
//        for(var f : fields){
//
//            int letters = f.length() - hiden;
//
//            if(f.length() < 6){
//                s.append(f).append(" ");
//                continue;
//            }
//
//
//            for(int i = 0; i < letters; i++){
//
//                s.append(f.charAt(i));
//            }
//
//            s.append(" ");
//        }
//
//        return s.toString();

        StringBuilder sb =  new StringBuilder();

        for(var f : fields){

            for(int i = 0; i < 4; i++){
                if(i >= f.length()) break;
                sb.append(f.charAt(i));
            }

            sb.append(" ");

        }

        return sb.toString();

    }

    public String linesWithInfos(){

        StringBuilder sb = new StringBuilder();

        for(var s : semesters){

            sb.append(s.date).append("#");

            for(var d : s.disciplines){

                if(Double.isNaN(d.average())) continue;

                sb.append(d.code).append(" ").append(d.name).append("#");

                int i = 1;

                if(d.scores == null){
                    sb.append("no grades yet#");
                    sb.append("Absences: ").append(d.absence).append("\n");
                    continue;
                }

                for(var scr : d.scores){
                    sb.append(i++).append(" - ").append(scr).append("#");
                }

                sb.append("Average: ").append(d.average()).append("#");
                sb.append("Absence: ").append(d.absence).append("\n");

            }

            sb.append("\n");

        }

        return sb.toString();

    }

    public String showAllInfos(){
        return informations.lineWithInfos.replaceAll("\n", "\n\n").replaceAll("#", "\n");
    }

    public String search(){

        Scanner sc = new Scanner(System.in);

        System.out.print("Your search: ");
        String content = sc.nextLine();

        Pattern p = Pattern.compile(".+" + content.toLowerCase(Locale.ROOT) +".+");

        String[] lines = informations.lineWithInfos.split("\n");

        StringBuilder sb = new StringBuilder();

        for(var line : lines){

            Matcher m = p.matcher(line.toLowerCase(Locale.ROOT));

            while(m.find()){
                sb.append(line).append("\n");
            }
        }

        if(sb.toString().isBlank()) return "\n" + RED + "not have result to this search\n" + RESET;

        return "\nResult: \n" + sb.toString().replaceAll("\n", "\n\n").replaceAll("#", "\n");
    }

    public void start(){

        Scanner sc = new Scanner(System.in);

        informations.allInfos = showAllInfos();

        while(true){

            System.out.println("[ 1 ] Show all information");
            System.out.println("[ 2 ] Show resume");
            System.out.println("[ 3 ] Search");
            System.out.println("[ 4 ] Probably IRA");
            System.out.println("[ 5 ] Personal information");
            System.out.println(RED + "[ 0 ] Exit" + RESET);

            System.out.print("your option: ");
            String op = sc.nextLine();

            int choise = isOption(op);

            if(choise == -1 || choise > 5){
                System.out.println(RED + "Option invalid, try again" + RESET);
                continue;
            }

            System.out.println();

            String content = "";

            switch (choise){

                case 0 -> {
                    return;
                }

                case 1 -> content = informations.allInfos;
                case 2 -> content = informations.resume;
                case 3 -> content = search();
                case 4 -> content = informations.expectedIra + "\n";
                case 5 -> content = informations.personalInfos;

            }
            System.out.println(GREEN + content + RESET);

        }

    }

    private int isOption(String op){
        try{
            return Integer.parseInt(op);
        }catch (Exception e){
            return -1;
        }
    }

}
