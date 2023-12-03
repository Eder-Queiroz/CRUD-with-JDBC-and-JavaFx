package vacinafx.controller;

import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import vacinafx.dao.VacinaDAO;
import vacinafx.dao.core.DAOFactory;
import vacinafx.dao.core.GenericJDBCDAO;
import vacinafx.model.Situacao;
import vacinafx.model.Vacina;

public class ControllerAuxiliar {

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSubmit;

    @FXML
    private TextField inputCodigo;

    @FXML
    private TextField inputDescricao;

    @FXML
    private TextField inputNome;

    @FXML
    private Label labelCodigo;

    @FXML
    private Label labelDescricao;

    @FXML
    private Label labelNome;

    @FXML
    private Label title;

    private DAOFactory daoFactory;

    private boolean edit;
    private Vacina vacina;

    @FXML
    void btnCancelClicado(ActionEvent event) {
        limparCampos();
        ((Button) event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    void btnSubmitClicado(ActionEvent event) {

        if (validarCampos()) {
            vacina.setNome(inputNome.getText());
            vacina.setDescricao(inputDescricao.getText());

            if (edit) {
                editar();
            } else {
                vacina.setSituacao(Situacao.ATIVO);
                create();
            }

            limparCampos();
            ((Button) event.getSource()).getScene().getWindow().hide();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Um ou mais campos estão vazios");
            alert.setTitle("Avizo");
            alert.setHeaderText("Preencha todos os campos");
            alert.showAndWait();
        }

    }

    public void setLabelText(String text) {
        title.setText(text);
    }

    public void setDAOFactory(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public void recebendo(Vacina vacina, boolean isEdit) {

        this.vacina = vacina;
        this.edit = isEdit;

        if (isEdit) {
            inputCodigo.setText(vacina.getCodigo().toString());
            inputDescricao.setText(vacina.getDescricao());
            inputNome.setText(vacina.getNome());
        } else {
            inputCodigo.disableProperty();
            inputCodigo.setVisible(false);
            labelCodigo.setVisible(false);
        }
    }

    private boolean validarCampos() {
        return !inputDescricao.getText().isEmpty() && !inputNome.getText().isEmpty();
    }

    private void limparCampos() {
        inputCodigo.setText("");
        inputDescricao.setText("");
        inputNome.setText("");
    }

    private void editar() {
        try {
            daoFactory.abrirConexao();
            VacinaDAO vacinaDAO = daoFactory.getDAO(VacinaDAO.class);
            vacinaDAO.update(vacina);
            System.out.println("\n\n Vacina Update \n\n");
        } catch (SQLException e) {
            GenericJDBCDAO.showSQLException(e);
        } finally {
            daoFactory.fecharConexao();
        }
    }

    private void create() {
        System.out.println("\n\n Vacina Create \n\n" + vacina + "\n\n");
        try {
            daoFactory.abrirConexao();
            VacinaDAO vacinaDAO = daoFactory.getDAO(VacinaDAO.class);
            vacinaDAO.create(vacina);
            System.out.println("\n\n Vacina Create \n\n");
        } catch (SQLException e) {
            GenericJDBCDAO.showSQLException(e);
        } finally {
            daoFactory.fecharConexao();
        }
    }

}
