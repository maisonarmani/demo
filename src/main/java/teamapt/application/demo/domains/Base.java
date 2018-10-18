package teamapt.application.demo.domains;


import lombok.Data;

@Data
public class Base {

    public Base(){
        System.out.println("Base is been constructed");
    }
}
