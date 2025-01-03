import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Representa um servidor no sistema
class Servidor {
    int id; // Identificador único do servidor
    boolean possuiToken; // Indica se o servidor possui o token
    boolean ativo; // Indica se o servidor está ativo
    boolean coordenador; // Indica se o servidor é o coordenador

    public Servidor(int id) {
        this.id = id;
        this.possuiToken = false;
        this.ativo = true;
        this.coordenador = false;
    }
}

// Implementa o Token Ring e Eleição do Anel
public class SistemaDistribuido {
    private List<Servidor> servidores; // Lista de servidores no anel
    private int indiceToken = 0; // Índice do servidor que possui o token

    public SistemaDistribuido(int numeroDeServidores) {
        servidores = new ArrayList<>();
        for (int i = 0; i < numeroDeServidores; i++) {
            servidores.add(new Servidor(i));
        }
        servidores.get(0).possuiToken = true; // Inicialmente, o primeiro servidor possui o token
        servidores.get(0).coordenador = true; // Inicialmente, o primeiro servidor é o coordenador
    }

    // Passa o token para o próximo servidor ativo no anel
    private void passarToken() {
        do {
            indiceToken = (indiceToken + 1) % servidores.size();
        } while (!servidores.get(indiceToken).ativo); // Encontra o próximo servidor ativo

        // Atualiza o estado do token
        for (Servidor servidor : servidores) {
            servidor.possuiToken = false;
        }
        servidores.get(indiceToken).possuiToken = true;
        System.out.println("Token passou para Servidor" + indiceToken);
    }

    // Simula a escrita no banco de dados
    public void escreverNoBanco(int idServidor) {
        Servidor servidor = servidores.get(idServidor);
        if (servidor.ativo && servidor.possuiToken) {
            System.out.println("Servidor " + idServidor + " está escrevendo no banco de dados.");
            passarToken(); // Passa o token após a escrita
        } else {
            System.out.println("Servidor " + idServidor + " não possui o token ou está inativo.");
        }
    }

    // Marca um servidor como inativo (falha) e redefine seu estado
    public void falharServidor(int idServidor) {
        Servidor servidor = servidores.get(idServidor);
        servidor.ativo = false;

        if (servidor.possuiToken) {
            System.out.println("Servidor " + idServidor + " estava com o token e falhou.");
            servidor.possuiToken = false;
            passarToken(); // Passa o token para o próximo servidor ativo
        } else {
            System.out.println("Servidor " + idServidor + " foi marcado como inativo.");
        }

        if (servidor.coordenador) {
            iniciarEleicao(); // Inicia a eleição se o coordenador falhar
        }
    }

    // Inicia o algoritmo de eleição do anel
    public void iniciarEleicao() {
        System.out.println("Iniciando eleição...");
        int novoCoordenadorId = -1;

        for (Servidor servidor : servidores) {
            if (servidor.ativo) {
                if (novoCoordenadorId == -1 || servidor.id > novoCoordenadorId) {
                    novoCoordenadorId = servidor.id;
                }
            }
        }

        // Atualiza o coordenador
        for (Servidor servidor : servidores) {
            servidor.coordenador = servidor.id == novoCoordenadorId;
        }
        System.out.println("Novo coordenador eleito: Servidor " + novoCoordenadorId);

        // Reinicia o token se necessário
        if (!servidores.get(indiceToken).ativo) {
            indiceToken = novoCoordenadorId;
            servidores.get(indiceToken).possuiToken = true;
            System.out.println("Token reemitido pelo novo coordenador.");
        }
    }

    // Reinicia um servidor
    public void reiniciarServidor(int idServidor) {
        Servidor servidor = servidores.get(idServidor);
        if (!servidor.ativo) {
            servidor.ativo = true;
            System.out.println("Servidor " + idServidor + " foi reiniciado e está ativo.");
        } else {
            System.out.println("Servidor " + idServidor + " já está ativo. Não é necessário reiniciá-lo.");
        }
    }

    // Mostra o estado do sistema
    public void exibirEstadoDoSistema() {
        System.out.println("Estado do sistema:");
        for (Servidor servidor : servidores) {
            System.out.println("Servidor " + servidor.id + " | Ativo: " + servidor.ativo + " | Coordenador: " + servidor.coordenador + " | Token: " + servidor.possuiToken);
        }
        mostrarDirecaoDoToken();
    }

    // Mostra a direção do token no anel, incluindo o último servidor passando para o primeiro ativo
    private void mostrarDirecaoDoToken() {
        System.out.print("Direção de passagem do token: ");
        List<Integer> servidoresAtivos = new ArrayList<>();
        for (Servidor servidor : servidores) {
            if (servidor.ativo) {
                servidoresAtivos.add(servidor.id);
            }
        }

        // Exibe a sequência de servidores ativos formando o anel
        for (int i = 0; i < servidoresAtivos.size(); i++) {
            System.out.print("Servidor" + servidoresAtivos.get(i));
            if (i < servidoresAtivos.size() - 1) {
                System.out.print(" -> ");
            }
        }
        // Conecta o último ao primeiro para formar o círculo
        if (!servidoresAtivos.isEmpty()) {
            System.out.print(" -> Servidor" + servidoresAtivos.get(0));
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Solicita o número de servidores
        System.out.print("Digite o número de servidores no sistema: ");
        int numeroDeServidores = scanner.nextInt();
        SistemaDistribuido sistema = new SistemaDistribuido(numeroDeServidores);

        // Menu
        boolean continuar = true;
        while (continuar) {
            System.out.println("\nMenu:");
            System.out.println("1 - Mostrar estado do sistema");
            System.out.println("2 - Simular falha de servidor");
            System.out.println("3 - Reiniciar servidor");
            System.out.println("4 - Tentar escrever no banco");
            System.out.println("5 - Sair");
            System.out.print("Escolha uma opção: ");
            int opcao = scanner.nextInt();

            switch (opcao) {
                case 1:
                    sistema.exibirEstadoDoSistema();
                    break;
                case 2:
                    System.out.print("Digite o ID do servidor que falhará: ");
                    int idFalha = scanner.nextInt();
                    sistema.falharServidor(idFalha);
                    break;
                case 3:
                    System.out.print("Digite o ID do servidor que será reiniciado: ");
                    int idReinicio = scanner.nextInt();
                    sistema.reiniciarServidor(idReinicio);
                    break;
                case 4:
                    System.out.print("Digite o ID do servidor que tentará escrever no banco: ");
                    int idEscrita = scanner.nextInt();
                    sistema.escreverNoBanco(idEscrita);
                    break;
                case 5:
                    continuar = false;
                    System.out.println("Encerrando o sistema...");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        }

        scanner.close();
    }
}
