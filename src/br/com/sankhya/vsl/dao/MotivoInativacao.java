package br.com.sankhya.vsl.dao;

import br.com.sankhya.modelcore.MGEModelException;

public enum MotivoInativacao {

    DESLIGAMENTO("1","Desligamento"),
    TROCAAREA("2","Troca de Área"),
    TROCAFUNCAO("3","Troca de Função"),
    CORRECAOMATRICULA("4","Correção de Matrícula"),
    BAIXA("5","Baixa"),
    SUBSTITUICAO("6","Substituição"),
    MATRICULAINCORRETA("7","Matrícula Incorreta"),
    CORRECAOCADASTRO("8","Correção de Cadastro");


    final private String opcao;


    MotivoInativacao(String opcao, String descricao) {
        this.opcao = opcao;
    }

    public String getDescricao() throws MGEModelException {
        switch (opcao){
            case "1" : return "Desligamento";
            case "2" : return "Troca de Área";
            case "3" : return "Troca de Função";
            case "4" : return "Correção de Matrícula";
            case "5" : return "Baixa";
            case "6" : return "Substituição";
            case "7" : return "Matrícula Incorreta";
            case "8" : return "Correção de Cadastro";

            default:
                throw new MGEModelException("Motivo não previsto");
        }
    }
}
