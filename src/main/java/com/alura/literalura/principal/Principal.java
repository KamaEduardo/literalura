package com.alura.literalura.principal;

import com.alura.literalura.dto.*;
import com.alura.literalura.model.*;
import com.alura.literalura.repository.*;
import com.alura.literalura.service.*;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {

    private Scanner leitura = new Scanner(System.in);

    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private LivroRepository livroRepository;
    private AutorRepository autorRepository;

    private final String ENDERECO = "https://gutendex.com/books/?search=";

    public Principal(LivroRepository livroRepository, AutorRepository autorRepository) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
    }

    public void exibirMenu(){

        int opcao = -1;

        while(opcao != 0){

            System.out.println("""

=============== LITERALURA ===============

1 - Buscar livro por título
2 - Listar todos os livros
3 - Listar livros por idioma
4 - Listar autores
5 - Autores vivos em determinado ano
6 - Estatísticas de idiomas

0 - Sair

=========================================

""");

            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao){

                case 1:
                    buscarLivro();
                    break;

                case 2:
                    listarLivros();
                    break;

                case 3:
                    listarLivrosPorIdioma();
                    break;

                case 4:
                    listarAutores();
                    break;

                case 5:
                    autoresVivosAno();
                    break;

                case 6:
                    estatisticasIdioma();
                    break;

                case 0:
                    System.out.println("Encerrando...");
                    break;

                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarLivro(){

        System.out.println("Digite o nome do livro:");
        String titulo = leitura.nextLine();

        String json = consumo.obterDados(ENDERECO + titulo.replace(" ","+"));

        DadosResultado resultado =
                conversor.obterDados(json, DadosResultado.class);

        Optional<DadosLivro> livroBuscado =
                resultado.livros().stream().findFirst();

        if(livroBuscado.isEmpty()){
            System.out.println("Livro não encontrado.");
            return;
        }

        DadosLivro dadosLivro = livroBuscado.get();

        DadosAutor dadosAutor = dadosLivro.autores().get(0);

        Optional<Autor> autorExistente =
                autorRepository.findByNome(dadosAutor.nome());

        Autor autor;

        if(autorExistente.isPresent()){

            autor = autorExistente.get();

        }else{

            autor = new Autor(
                    dadosAutor.nome(),
                    dadosAutor.nascimento(),
                    dadosAutor.falecimento()
            );

            autorRepository.save(autor);
        }

        Livro livro = new Livro();
        livro.setTitulo(dadosLivro.titulo());
        livro.setIdioma(dadosLivro.idiomas().get(0));
        livro.setDownloads(dadosLivro.downloads());
        livro.setAutor(autor);

        livroRepository.save(livro);

        System.out.println("Livro salvo com sucesso!");
        System.out.println(livro);
    }

    private void listarLivros(){

        List<Livro> livros = livroRepository.findAll();

        livros.forEach(System.out::println);
    }

    private void listarLivrosPorIdioma(){

        System.out.println("""
Digite idioma:

en - inglês
pt - português
es - espanhol
fr - francês
""");

        String idioma = leitura.nextLine();

        List<Livro> livros = livroRepository.findByIdioma(idioma);

        if(livros.isEmpty()){
            System.out.println("Nenhum livro encontrado");
        }

        livros.forEach(System.out::println);
    }

    private void listarAutores(){

        List<Autor> autores = autorRepository.findAll();

        autores.forEach(System.out::println);
    }

    private void autoresVivosAno(){

        System.out.println("Digite o ano:");
        int ano = leitura.nextInt();

        List<Autor> autores =
                autorRepository
                        .findByAnoNascimentoLessThanEqualAndAnoFalecimentoGreaterThanEqual(ano,ano);

        autores.forEach(System.out::println);
    }

    private void estatisticasIdioma(){

        System.out.println("""
Digite idioma para estatística:

en
pt
es
fr
""");

        String idioma = leitura.nextLine();

        long quantidade = livroRepository.countByIdioma(idioma);

        System.out.println("Quantidade de livros: " + quantidade);
    }
}