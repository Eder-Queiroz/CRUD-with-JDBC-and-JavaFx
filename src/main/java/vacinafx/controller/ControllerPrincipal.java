package vacinafx.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import vacinafx.dao.AplicacaoDAO;
import vacinafx.dao.PessoaDAO;
import vacinafx.dao.VacinaDAO;
import vacinafx.dao.core.DAOFactory;
import vacinafx.dao.core.GenericJDBCDAO;
import vacinafx.model.Aplicacao;
import vacinafx.model.Pessoa;
import vacinafx.model.Situacao;
import vacinafx.model.Vacina;

public class ControllerPrincipal implements Initializable {

    @FXML
    private Button botaoEditar;

    @FXML
    private Button botaoNova;

    @FXML
    private Button botaoPesquisar;

    @FXML
    private Button botaoPesquisarPessoa;

    @FXML
    private Button botaoRemover;

    @FXML
    private Button botaoAplicacao;

    @FXML
    private TableColumn<Vacina, Long> colunaCodigo;

    @FXML
    private TableColumn<Pessoa, Long> colunaCodigoPessoa;

    @FXML
    private TableColumn<Pessoa, String> colunaCpfPessoa;

    @FXML
    private TableColumn<Vacina, String> colunaDescricao;

    @FXML
    private TableColumn<Pessoa, LocalDate> colunaNascimentoPessoa;

    @FXML
    private TableColumn<Vacina, String> colunaNome;

    @FXML
    private TableColumn<Pessoa, String> colunaNomePessoa;

    @FXML
    private DatePicker inputApartirNascimento;

    @FXML
    private DatePicker inputAteNascimento;

    @FXML
    private TextField inputCodigo;

    @FXML
    private TextField inputCodigoPessoa;

    @FXML
    private TextField inputDescricao;

    @FXML
    private TextField inputCpfPessoa;

    @FXML
    private TextField inputNome;

    @FXML
    private TextField inputNomePessoa;

    @FXML
    private Label labelApartirDe;

    @FXML
    private Label labelAte;

    @FXML
    private Label labelCodigo;

    @FXML
    private Label labelCodigoPessoa;

    @FXML
    private Label labelDescricao;

    @FXML
    private Label labelCpfPessoa;

    @FXML
    private Label labelNascimento;

    @FXML
    private Label labelNome;

    @FXML
    private Label labelNomePessoa;

    @FXML
    private TableView<Vacina> table;

    @FXML
    private TableView<Pessoa> tablePessoa;

    private DAOFactory daoFactory;

    private Stage stageAuxiliar;
    private ControllerAuxiliar controllerAuxiliar;

    List<Vacina> vacinasBuscadas;
    List<Pessoa> pessoasBuscadas;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vacinafx/JanelaAuxiliar.fxml"));
        Parent root;

        try {
            root = loader.load();
            Scene scene = new Scene(root);
            stageAuxiliar = new Stage();
            stageAuxiliar.getIcons().add(new Image(getClass().getResourceAsStream("/images/java.png")));
            stageAuxiliar.setScene(scene);
            controllerAuxiliar = loader.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }

        colunaCodigo.setCellValueFactory(new PropertyValueFactory<Vacina, Long>("codigo"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<Vacina, String>("nome"));
        colunaDescricao.setCellValueFactory(new PropertyValueFactory<Vacina, String>("descricao"));

        colunaCodigoPessoa.setCellValueFactory(new PropertyValueFactory<Pessoa, Long>("codigo"));
        colunaNomePessoa.setCellValueFactory(new PropertyValueFactory<Pessoa, String>("nome"));
        colunaCpfPessoa.setCellValueFactory(new PropertyValueFactory<Pessoa, String>("cpf"));
        colunaNascimentoPessoa.setCellValueFactory(new PropertyValueFactory<Pessoa, LocalDate>("nascimento"));
    }

    public void atualizarTela() {
        vacinasBuscadas = buscarTodasVacinas();
        pessoasBuscadas = buscarTodasPessoas();
        table.getItems().addAll(vacinasBuscadas);
        tablePessoa.getItems().addAll(pessoasBuscadas);
    }

    @FXML
    void editarClicado(ActionEvent event) {
        System.out.println("\n\n Editar \n\n");
        if (table.getSelectionModel().getSelectedItem() != null) {
            Vacina vacina = table.getSelectionModel().getSelectedItem();
            System.out.println("\n\n" + vacina + "\n\n");
            controllerAuxiliar.recebendo(vacina, true);
            controllerAuxiliar.setDAOFactory(daoFactory);
            controllerAuxiliar.setLabelText("Editar Vacina");
            stageAuxiliar.setTitle("Editar Vacina");
            stageAuxiliar.showAndWait();

            table.getItems().clear();
            vacinasBuscadas = buscarTodasVacinas();
            table.getItems().addAll(vacinasBuscadas);

        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Selecione uma vacina");
            alert.setTitle("Avizo Edição");
            alert.setHeaderText("Selecione uma vacina para editar");
            alert.showAndWait();
        }

    }

    @FXML
    void novaClicado(ActionEvent event) {
        controllerAuxiliar.recebendo(new Vacina(), false);
        controllerAuxiliar.setDAOFactory(daoFactory);
        controllerAuxiliar.setLabelText("Nova Vacina");
        stageAuxiliar.setTitle("Nova Vacina");
        stageAuxiliar.showAndWait();

        table.getItems().clear();
        vacinasBuscadas = buscarTodasVacinas();
        table.getItems().addAll(vacinasBuscadas);
    }

    @FXML
    void pesquisarClicado(ActionEvent event) {
        String codigo = inputCodigo.getText();
        String nome = inputNome.getText();
        String descricao = inputDescricao.getText();

        vacinasBuscadas = buscarVacina(codigo, nome, descricao);
        table.getItems().clear();
        table.getItems().addAll(vacinasBuscadas);
    }

    @FXML
    void pesquisarClicadoNascimento(ActionEvent event) {

        String codigo = inputCodigoPessoa.getText();
        String nome = inputNomePessoa.getText();
        String cpf = inputCpfPessoa.getText();
        LocalDate apartirData = inputApartirNascimento.getValue();
        LocalDate ateData = inputAteNascimento.getValue();

        pessoasBuscadas = buscarPessoa(codigo, nome, cpf, apartirData, ateData);
        tablePessoa.getItems().clear();
        tablePessoa.getItems().addAll(pessoasBuscadas);

    }

    @FXML
    void removerClicado(ActionEvent event) {

        if (table.getSelectionModel().getSelectedItem() != null) {
            Vacina vacina = table.getSelectionModel().getSelectedItem();
            ButtonType sim = new ButtonType("Sim", ButtonBar.ButtonData.OK_DONE);
            ButtonType nao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert alert = new Alert(AlertType.CONFIRMATION, "Deseja Mesmo Remover a Vacina?", sim, nao);
            alert.setTitle("Remoção");
            alert.setHeaderText("Vacina: " + vacina.getNome());

            Optional<ButtonType> option = alert.showAndWait();

            if (option.get().equals(sim)) {
                removeVacina(vacina);
                table.getItems().clear();
                vacinasBuscadas = buscarTodasVacinas();
                table.getItems().addAll(vacinasBuscadas);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Selecione uma vacina");
            alert.setTitle("Avizo Remoção");
            alert.setHeaderText("Selecione uma vacina para remover");
            alert.showAndWait();
        }

    }

    @FXML
    void botaoAplicacaoClicado(ActionEvent event) {
        if (table.getSelectionModel().getSelectedItem() != null
                && tablePessoa.getSelectionModel().getSelectedItem() != null) {
            Vacina vacina = table.getSelectionModel().getSelectedItem();
            Pessoa pessoa = tablePessoa.getSelectionModel().getSelectedItem();
            Aplicacao aplicacao = new Aplicacao(LocalDate.now(), pessoa, vacina, Situacao.ATIVO);
            createAplicacao(aplicacao);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Aplicação");
            alert.setHeaderText("Aplicação Feita com Sucesso");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Selecione uma Pessoa e uma Vacina");
            alert.setTitle("Aplicação");
            alert.setHeaderText("Sem Pessoa e Vacina para Aplicar");
            alert.showAndWait();
        }
    }

    public void setDAOFactory(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    private List<Vacina> buscarVacina(String codigo, String nome, String descricao) {
        try {
            daoFactory.abrirConexao();
            VacinaDAO vacinaDAO = daoFactory.getDAO(VacinaDAO.class);
            List<Vacina> vacinas = vacinaDAO.findWithFilter(codigo, nome, descricao);
            System.out.println("\n\n" + vacinas + "\n\n");
            return vacinas;
        } catch (SQLException e) {
            GenericJDBCDAO.showSQLException(e);
        } finally {
            daoFactory.fecharConexao();
        }
        return null;

    }

    private List<Vacina> buscarTodasVacinas() {
        try {
            daoFactory.abrirConexao();
            VacinaDAO vacinaDAO = daoFactory.getDAO(VacinaDAO.class);
            List<Vacina> vacinas = vacinaDAO.findAll();
            for (Vacina vacina : vacinas) {
                System.out.println(vacina);
            }
            return vacinas;
        } catch (SQLException e) {
            GenericJDBCDAO.showSQLException(e);
        } finally {
            daoFactory.fecharConexao();
        }
        return null;

    }

    private List<Pessoa> buscarTodasPessoas() {
        try {
            daoFactory.abrirConexao();
            PessoaDAO pessoaDAO = daoFactory.getDAO(PessoaDAO.class);
            List<Pessoa> pessoas = pessoaDAO.findAll();
            for (Pessoa vacina : pessoas) {
                System.out.println(vacina);
            }
            return pessoas;
        } catch (SQLException e) {
            GenericJDBCDAO.showSQLException(e);
        } finally {
            daoFactory.fecharConexao();
        }
        return null;

    }

    private List<Pessoa> buscarPessoa(String codigo, String nome, String cpf, LocalDate apartirData,
            LocalDate ateData) {
        try {
            daoFactory.abrirConexao();
            PessoaDAO pessoaDAO = daoFactory.getDAO(PessoaDAO.class);
            List<Pessoa> pessoas = pessoaDAO.findWithFilter(codigo, nome, cpf, apartirData, ateData);
            System.out.println("\n\n" + pessoas + "\n\n");
            return pessoas;
        } catch (SQLException e) {
            GenericJDBCDAO.showSQLException(e);
        } finally {
            daoFactory.fecharConexao();
        }
        return null;

    }

    private void removeVacina(Vacina vacina) {
        try {
            daoFactory.abrirConexao();
            VacinaDAO vacinaDAO = daoFactory.getDAO(VacinaDAO.class);
            vacinaDAO.removeVacina(vacina);
            System.out.println("\n\n Vacina Remove \n\n");
        } catch (SQLException e) {
            GenericJDBCDAO.showSQLException(e);
        } finally {
            daoFactory.fecharConexao();
        }
    }

    private void createAplicacao(Aplicacao aplicacao) {
        try {
            daoFactory.abrirConexao();
            AplicacaoDAO aplicacaoDAO = daoFactory.getDAO(AplicacaoDAO.class);
            aplicacaoDAO.createAplicacao(aplicacao);
            System.out.println("\n\n Aplicação Criada \n\n");
        } catch (SQLException e) {
            GenericJDBCDAO.showSQLException(e);
        } finally {
            daoFactory.fecharConexao();
        }
    }

}
