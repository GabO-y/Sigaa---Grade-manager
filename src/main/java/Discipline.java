import java.util.List;

public class Discipline {

    String code;
    String name;
    List<Double> scores;
    int absence;
    Double avg;

    public Discipline(String code, String name, List<Double> scores, int absence, Double avg) {
        this.code = code;
        this.name = name;
        this.scores = scores;
        this.absence = absence;
        this.avg = avg;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        if(scores == null){
            sb.append("without grades");
        }else{

        for(var s : scores){
           sb.append(s).append(", ");
        }

        }

        sb.append("\b\b");

        return code + " " + name + " ( " + sb + " ): [ " + avg + " ] " + " | " + absence;
    }

    public double average(){
        if(scores == null) return  0.0;
        int count = 0;
        double sum = 0.0;
        int i = 4;
        for(var s : scores){
            sum += s * i;
            count += i++;
        }

        return sum/count;
    }
}
