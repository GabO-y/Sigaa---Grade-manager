import java.math.BigDecimal;
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

    public static final String tabsMenu = "\t\t\t\t\t\t";
    public static final String tabs = "\t\t\t\t\t\t";

    public static final String GREEN = "\u001B[32m";
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String BLUE = "\u001B[34m";

    public Main(String content){
        init(content);
        informations = new Informations(this);
    }

    public Main(){}

    public void read(String content){
        init(content);
    }

    public void init(String content){

        content = content.replace("\t", " ");

        Pattern year = Pattern.compile("\\d{4}[.]\\d");
        Pattern code = Pattern.compile("[A-Z]{3}\\d{4}");
        Pattern name = Pattern.compile(" [A-Za-z].+\\(\\d{8}\\)");
        Pattern uce = Pattern.compile("UNIDADE CURRICULAR DE EXTENSÃO-UCE");
        Pattern number = Pattern.compile(" \\d+.+\\d+|-- 0 --");
        Pattern studentCourse = Pattern.compile("Curso:\\s*(.*)");

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

    public String allAvgSemester(){

        StringBuilder content = new StringBuilder();

        for(var s : semesters){
            content.append(tabs).append(s.date).append(": ").append(Double.isNaN(s.ira()) ? "no grades yet" :
                String.format("%.2f", s.ira())
            ).append("\n");
        }

        return content.toString();
    }

    public String expectedIra(){
        double sum = 0.0;
        int count = 0;
        for(var s : semesters){
            if(Double.isNaN(s.ira())) continue;
            sum += s.ira();
            count++;
        }
        return tabs + "IRA: " + String.format("%.1f", sum/count);
    }

    public String personalInfos(){

        StringBuilder infos = new StringBuilder();

        infos.append(student.name()).append("\n");
        infos.append(student.course()).append("\n\n");

        Table t = new Table("Infos");

        for(var s : semesters){

            t.addRown(s.date);
            t.addColumn("enrolled", Integer.toString(s.totalEnrolled()));
            t.addColumn("absences",  s.totalAbsences().toString());
            t.addColumn("avg", s.ira().toString());

        }

        return infos + t.table();
    }

    public String personalTable(){



        return "";

    }

    public String resume(){

        List<String> resume = new ArrayList<>(Arrays.stream(resumeAll().split("\n\n")).toList());
        resume.remove(resume.size() - 1);
        StringBuilder sb = new StringBuilder();

        int size = maxBorder(resume);

        for(var s : resume){

            String[] split = s.split("\n");

            for(var line : split){
                sb.append(tabs).append(normalizeLine(line, size)).append("\n");
            }


        }

        return sb.toString();

    }

    public String normalizeLine(String line, int size){

        StringBuilder sb = new StringBuilder();

        if(line.length() == 6){

            boolean aux = true;

            while(sb.length() != size * 2){

                if(sb.length() / 0.8 >= size && aux){
                    sb.append(line);
                    aux = false;
                }else{
                    sb.append("||");
                }

            }

            return sb.toString();
        }


        Pattern left = Pattern.compile(".+\\|\\|");
        Pattern right = Pattern.compile("\\|\\|.+");

        Matcher mLeft = left.matcher(line);
        Matcher mRight = right.matcher(line);

        if(mLeft.find()){

            sb.append(mLeft.group().replaceAll(" +\\|\\|", ""));

            while(sb.length() != size){
                sb.append(" ");
            }

        }

        if(mRight.find()){
            sb.append(" ").append(mRight.group());
        }

        return sb.toString();

    }

    public int maxBorder(List<String> list){

        Pattern p = Pattern.compile(".+\\|\\|");
        int max = 0;

        for(var fields : list){

            String[] lines = fields.split("\n");

            for(var line : lines){

            Matcher m = p.matcher(line);

            if(m.find()){

                String temp = m.group().replace(" ||", "");
                temp = temp.replaceAll(" +$", "");

                if(max < temp.length()){
                    max = temp.length();
                }

            }

        }

        }

        return max;

    }

    public String resumeAll(){

        StringBuilder str = new StringBuilder();

        for(var s : semesters){

            str.append(s.date).append("\n");

            for(var d : s.disciplines){

                String sort = shorten(d.name);

                if(sort.isBlank()) continue;

                str.append(sort).append(" || ");

                if(d.scores == null) {
                    str.append("no grades yet").append("\n");
                    continue;
                }

//                for (var score : d.scores){
//                    str.append(score).append(" | ");
//                }
//
//                str.append("\b -> ").append(String.format("%.1f\n", d.average()));

                str.append(organizeGrade(d.scores))
                    .append(" -> ")
                    .append(String.format("%.1f", d.average()))
                    .append("\n");

            }

            str.append("\n\n");

        }

        return str.toString();

    }

    private String organizeGrade(List<Double> scores){

        StringBuilder sb = new StringBuilder();

//        for(var s : scores){
//
//            StringBuilder current = new StringBuilder(" " + s);
//
//            while(current.length() != 6){
//                current.append(" ");
//            }
//
//            sb.append(current).append(" | ");
//
//        }

        for(int i = 0; i < 3; i++){

            StringBuilder current = new StringBuilder();

            if(i < scores.size()){
                current.append(scores.get(i));
            }

            while(current.length() != 6){
                current.append(" ");
            }

            sb.append(current).append("| ");

        }

        return sb.toString();
    }

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
        return tabs + informations.lineWithInfos.replaceAll("\n", "\n\n" + tabs).replaceAll("#", "\n" + tabs);
    }

    public String search(){

        Scanner sc = new Scanner(System.in);

        System.out.print(tabs + "Your search: ");
        String content = sc.nextLine();

        Pattern p = Pattern.compile(".+" + content.toLowerCase(Locale.ROOT) +".+");

        String[] lines = informations.lineWithInfos.split("\n");

        StringBuilder sb = new StringBuilder();

        for(var line : lines){

            Matcher m = p.matcher(line.toLowerCase(Locale.ROOT));

            while(m.find()){
                sb.append(tabs).append(line).append("\n");
            }
        }

        if(sb.toString().isBlank()) return "\n" + tabs + RED + "not have result to this search\n" + RESET;

        return "\n" + tabs + "Result: \n" + sb.toString().replaceAll("\n", "\n\n").replaceAll("#", "\n" + tabs);
    }

    public void start(){

        Scanner sc = new Scanner(System.in);

        informations.allInfos = showAllInfos();

        while(true){

            System.out.println(tabsMenu + "[ 1 ] Show all information");
            System.out.println(tabsMenu + "[ 2 ] Show resume");
            System.out.println(tabsMenu + "[ 3 ] Search");
            System.out.println(tabsMenu + "[ 4 ] Average of all semesters");
            System.out.println(tabsMenu + "[ 5 ] Probably IRA");
            System.out.println(tabsMenu + "[ 6 ] Personal information");
            System.out.println(tabsMenu + RED + "[ 0 ] Exit" + RESET);

            System.out.print(tabsMenu + "your option: ");
            String op = sc.nextLine();

            int choise = isOption(op);

            if(choise == -1 || choise > 6){
                System.out.println(tabs + RED + "Option invalid, try again" + RESET);
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
                case 4 -> content = informations.avgPerSemester;
                case 5 -> content = informations.expectedIra + "\n";
                case 6 -> content = informations.personalInfos;

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
