package pt.jorgermduarte.isecid.aulas.aula1;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Aluno {
    private String nome;
    private int numero;
    private int idade;
    private String curso;


    public void printAluno(){
        System.out.println("Dados do aluno: ");
        System.out.println("Nome: " + this.nome);
        System.out.println("Numero: " + this.numero);
        System.out.println("Idade: " + this.idade);
        System.out.println("Curso: " + this.curso);
    }
}
