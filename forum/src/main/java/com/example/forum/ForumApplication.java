package com.example.forum;

import java.sql.*;
import java.util.Scanner;

public class ForumApplication {
	private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "2004"; // Substitua pela senha do MySQL

	private static Connection connection;
	private static Scanner scanner = new Scanner(System.in);
	private static int userId; // ID do usuário logado

	public static void main(String[] args) {
		try {
			connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
			System.out.println("Conectado ao banco de dados!");

			while (true) {
				System.out.println("=== Fórum ===");
				System.out.println("1. Login");
				System.out.println("2. Registrar");
				System.out.println("3. Sair");
				System.out.print("Escolha uma opção: ");
				int choice = scanner.nextInt();
				scanner.nextLine(); // Limpar buffer

				switch (choice) {
					case 1 -> login();
					case 2 -> register();
					case 3 -> {
						System.out.println("Saindo do sistema...");
						return;
					}
					default -> System.out.println("Opção inválida.");
				}
			}
		} catch (SQLException e) {
			System.out.println("Erro de conexão: " + e.getMessage());
		}
	}

	private static void login() {
		try {
			System.out.print("Digite seu email: ");
			String email = scanner.nextLine();
			System.out.print("Digite sua senha: ");
			String senha = scanner.nextLine();

			String query = "SELECT idusuario, nome FROM usuario WHERE email = ? AND senha = ?";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setString(1, email);
			stmt.setString(2, senha);

			ResultSet rs = stmt.executeQuery();  // Potencial exceção de SQL
			if (rs.next()) {
				userId = rs.getInt("idusuario");
				String userName = rs.getString("nome");
				System.out.println("Bem-vindo, " + userName + "!");
				mainMenu(userId);  // Passando o userId para o método
			} else {
				System.out.println("Credenciais inválidas.");
			}
		} catch (SQLException e) {
			System.out.println("Erro no login: " + e.getMessage());
		}
	}


	private static void register() {
		try {
			System.out.print("Digite seu nome: ");
			String nome = scanner.nextLine();
			System.out.print("Digite seu email: ");
			String email = scanner.nextLine();
			System.out.print("Digite sua senha: ");
			String senha = scanner.nextLine();

			String query = "INSERT INTO usuario (nome, email, senha, perfil_idperfil) VALUES (?, ?, ?, ?)";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setString(1, nome);
			stmt.setString(2, email);
			stmt.setString(3, senha);
			stmt.setInt(4, 1); // ou qualquer outro valor válido para perfil_idperfil
			stmt.executeUpdate();

			System.out.println("Usuário registrado com sucesso!");
		} catch (SQLException e) {
			System.out.println("Erro no registro: " + e.getMessage());
		}
	}

	private static void mainMenu(int userId) {
		while (true) {
			System.out.println("\n=== Menu Principal ===");
			System.out.println("1. Criar Tópico");
			System.out.println("2. Ver Tópicos");
			System.out.println("3. Sair");
			System.out.print("Escolha uma opção: ");
			int choice = scanner.nextInt();
			scanner.nextLine(); // Limpar buffer

			switch (choice) {
				case 1 -> createTopic(userId); // Chama o método para criar tópico
				case 2 -> viewTopics(userId); // Passar userId para a função
				case 3 -> {
					System.out.println("Saindo para o menu inicial...");
					return;
				}
				default -> System.out.println("Opção inválida.");
			}
		}
	}

	private static void createTopic(int userId) {
		try {
			System.out.print("Digite o título do tópico: ");
			String titulo = scanner.nextLine();

			System.out.print("Digite o conteúdo do tópico (pergunta/descição): ");
			String conteudo = scanner.nextLine();

			System.out.println("Escolha o curso relacionado a este tópico:");
			// Exibindo os cursos disponíveis
			String cursoQuery = "SELECT idcurso, nome FROM curso";
			Statement stmtCurso = connection.createStatement();
			ResultSet rsCurso = stmtCurso.executeQuery(cursoQuery);  // Potencial exceção de SQL

			int cursoId = 0;
			while (rsCurso.next()) {
				int id = rsCurso.getInt("idcurso");
				String nome = rsCurso.getString("nome");
				System.out.printf("[%d] %s\n", id, nome);
			}

			System.out.print("Digite o ID do curso: ");
			cursoId = scanner.nextInt();
			scanner.nextLine();  // Limpar buffer

			// Validação do ID do curso
			if (!isValidCourse(cursoId)) {
				System.out.println("Curso inválido! Tópico não criado.");
				return;
			}

			// Inserir o tópico no banco de dados
			String query = "INSERT INTO topico (titulo, conteudo, autor_id, curso_idcurso, autor_nome) VALUES (?, ?, ?, ?, ?)";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setString(1, titulo);
			stmt.setString(2, conteudo);
			stmt.setInt(3, userId);  // Autor será o usuário logado
			stmt.setInt(4, cursoId);  // Associa o curso ao tópico
			stmt.setString(5, getUserNameById(userId));  // Define o nome do autor

			int rowsAffected = stmt.executeUpdate();  // Potencial exceção de SQL
			if (rowsAffected > 0) {
				System.out.println("Tópico criado com sucesso!");
			} else {
				System.out.println("Erro ao criar o tópico.");
			}

		} catch (SQLException e) {
			System.out.println("Erro ao criar tópico: " + e.getMessage());
		}
	}

	private static boolean isValidCourse(int cursoId) {
		try {
			String query = "SELECT COUNT(*) FROM curso WHERE idcurso = ?";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setInt(1, cursoId);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getInt(1) > 0;  // Retorna verdadeiro se o curso existe
			}
		} catch (SQLException e) {
			System.out.println("Erro ao validar curso: " + e.getMessage());
		}
		return false;
	}

	private static String getUserNameById(int userId) {
		try {
			String query = "SELECT nome FROM usuario WHERE idusuario = ?";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getString("nome");
			}
		} catch (SQLException e) {
			System.out.println("Erro ao obter o nome do usuário: " + e.getMessage());
		}
		return "Desconhecido";
	}

	private static void viewTopics(int userId) {
		try {
			String query = "SELECT idtopico, titulo, conteudo, autor_nome FROM topico WHERE estado_topico = 'ativo'";
			PreparedStatement stmt = connection.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();

			System.out.println("\n=== Tópicos Disponíveis ===");
			int i = 1;
			while (rs.next()) {
				int topicId = rs.getInt("idtopico");
				String titulo = rs.getString("titulo");
				String conteudo = rs.getString("conteudo");
				String autorNome = rs.getString("autor_nome");
				System.out.printf("[%d] ID: %d | Título: %s | Autor: %s\n", i++, topicId, titulo, autorNome);
			}

			System.out.print("\nDigite o ID do tópico para ver mais detalhes: ");
			int topicChoice = scanner.nextInt();
			scanner.nextLine();  // Limpar o buffer

			// Verifica se o tópico existe
			String checkTopicQuery = "SELECT COUNT(*) FROM topico WHERE idtopico = ?";
			PreparedStatement checkStmt = connection.prepareStatement(checkTopicQuery);
			checkStmt.setInt(1, topicChoice);
			ResultSet checkRs = checkStmt.executeQuery();
			if (checkRs.next() && checkRs.getInt(1) > 0) {
				// Chama a função para ver os detalhes do tópico
				viewTopicDetails(topicChoice, userId);
			} else {
				System.out.println("Tópico não encontrado!");
			}

		} catch (SQLException e) {
			System.out.println("Erro ao carregar tópicos: " + e.getMessage());
		}
	}

	private static void viewTopicDetails(int topicId, int userId) {
		try {
			// Consulta para pegar o título, conteúdo, autor_id e outras informações do tópico
			String query = "SELECT titulo, conteudo, autor_id FROM topico WHERE idtopico = ?";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setInt(1, topicId);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				String titulo = rs.getString("titulo");
				String conteudo = rs.getString("conteudo");
				int autorId = rs.getInt("autor_id");

				// Exibe os detalhes do tópico
				System.out.println("\n=== Detalhes do Tópico ===");
				System.out.println("Título: " + titulo);
				System.out.println("Conteúdo (Pergunta): " + conteudo);

				// Exibe as respostas associadas ao tópico
				viewResponses(topicId);

				// Se o usuário for o autor do tópico, exibe o menu de opções de edição ou exclusão
				if (autorId == userId) {
					// Se for o autor, exibe o menu com opções para atualizar ou deletar o tópico
					topicMenu(topicId, userId);
				} else {
					// Se não for o autor, apenas responder
					System.out.println("1. Responder");
					int choice = scanner.nextInt();
					scanner.nextLine(); // Limpar buffer
					if (choice == 1) {
						createAnswer(topicId, userId);
					}
				}
			} else {
				System.out.println("Tópico não encontrado.");
			}
		} catch (SQLException e) {
			System.out.println("Erro ao visualizar detalhes do tópico: " + e.getMessage());
		}
	}

	private static void topicMenu(int topicId, int userId) {
        System.out.println("\nEscolha uma opção:");
        System.out.println("1. Responder");
        System.out.println("2. Visualizar respostas");
        System.out.println("3. Editar Tópico (somente autor)");
        System.out.println("4. Deletar Tópico (somente autor)");
        System.out.print("Escolha uma opção: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer

        switch (choice) {
            case 1 -> createAnswer(topicId, userId);
            case 2 -> viewResponses(topicId);
            case 3 -> {
                if (isTopicAuthor(topicId, userId)) {
                    updateTopic(topicId);
                } else {
                    System.out.println("Você não é o autor deste tópico.");
                }
            }
            case 4 -> {
                if (isTopicAuthor(topicId, userId)) {
                    deleteTopic(topicId);
                } else {
                    System.out.println("Você não é o autor deste tópico.");
                }
            }
            default -> System.out.println("Opção inválida.");
        }
    }

	private static boolean isTopicAuthor(int topicId, int userId) {
		try {
			String query = "SELECT autor_id FROM topico WHERE idtopico = ?";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setInt(1, topicId);
			ResultSet rs = stmt.executeQuery();  // Potencial exceção de SQL

			if (rs.next()) {
				return rs.getInt("autor_id") == userId;
			}
		} catch (SQLException e) {
			System.out.println("Erro ao verificar autor: " + e.getMessage());
		}
		return false;
	}

	private static void updateTopic(int topicId) {
		try {
			System.out.print("Digite o novo título: ");
			String titulo = scanner.nextLine();

			System.out.print("Digite o novo conteúdo: ");
			String conteudo = scanner.nextLine();

			String query = "UPDATE topico SET titulo = ?, conteudo = ? WHERE idtopico = ?";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setString(1, titulo);
			stmt.setString(2, conteudo);
			stmt.setInt(3, topicId);
			stmt.executeUpdate();

			System.out.println("Tópico atualizado com sucesso.");
		} catch (SQLException e) {
			System.out.println("Erro ao atualizar tópico: " + e.getMessage());
		}
	}

	private static void deleteTopic(int topicId) {
		try {
			// Primeiro, exclua as respostas associadas ao tópico
			String deleteResponsesQuery = "DELETE FROM resposta WHERE topico_idtopico = ?";
			PreparedStatement stmtResponses = connection.prepareStatement(deleteResponsesQuery);
			stmtResponses.setInt(1, topicId);
			int rowsAffectedResponses = stmtResponses.executeUpdate();

			// Verifica se houve erro ao excluir respostas
			if (rowsAffectedResponses > 0) {
				System.out.println("Respostas excluídas com sucesso!");
			} else {
				System.out.println("Nenhuma resposta encontrada para excluir.");
			}

			// Agora, exclua o tópico
			String deleteTopicQuery = "DELETE FROM topico WHERE idtopico = ?";
			PreparedStatement stmtTopic = connection.prepareStatement(deleteTopicQuery);
			stmtTopic.setInt(1, topicId);
			int rowsAffectedTopic = stmtTopic.executeUpdate();

			if (rowsAffectedTopic > 0) {
				System.out.println("Tópico excluído com sucesso!");

				// Após excluir o tópico, redefinir o valor do AUTO_INCREMENT para recomeçar os IDs
				String resetAutoIncrementQuery = "ALTER TABLE topico AUTO_INCREMENT = 1";
				Statement stmtReset = connection.createStatement();
				stmtReset.executeUpdate(resetAutoIncrementQuery);
				System.out.println("Contagem de IDs reiniciada.");
			} else {
				System.out.println("Erro ao excluir o tópico.");
			}

		} catch (SQLException e) {
			System.out.println("Erro ao excluir tópico: " + e.getMessage());
		}
	}

	private static void createAnswer(int topicId, int userId) {
		try {
			// Solicita ao usuário o conteúdo da resposta
			System.out.print("Digite o conteúdo da resposta: ");
			String conteudoResposta = scanner.nextLine();

			// Insere a resposta no banco de dados
			String query = "INSERT INTO resposta (conteudo_resposta, usuario_idusuario, topico_idtopico) VALUES (?, ?, ?)";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setString(1, conteudoResposta);  // Conteúdo da resposta
			stmt.setInt(2, userId);               // ID do usuário logado
			stmt.setInt(3, topicId);              // ID do tópico ao qual a resposta pertence

			// Executa a inserção
			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("Resposta enviada com sucesso!");
			} else {
				System.out.println("Erro ao enviar a resposta.");
			}

		} catch (SQLException e) {
			System.out.println("Erro ao criar resposta: " + e.getMessage());
		}
	}

	private static void viewResponses(int topicId) {
		try {
			String query = "SELECT r.conteudo_resposta, r.data_resposta, u.nome " +
					"FROM resposta r JOIN usuario u ON r.usuario_idusuario = u.idusuario WHERE r.topico_idtopico = ?";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setInt(1, topicId);
			ResultSet rs = stmt.executeQuery();

			System.out.println("\n=== Respostas ===");
			while (rs.next()) {
				String conteudoResposta = rs.getString("conteudo_resposta");
				String dataResposta = rs.getString("data_resposta");
				String autorResposta = rs.getString("nome");
				System.out.printf("Resposta de %s em %s: %s\n", autorResposta, dataResposta, conteudoResposta);
			}

		} catch (SQLException e) {
			System.out.println("Erro ao visualizar respostas: " + e.getMessage());
		}
	}
}
