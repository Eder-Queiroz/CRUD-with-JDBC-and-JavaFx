package vacinafx.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import vacinafx.dao.core.GenericJDBCDAO;
import vacinafx.model.Situacao;
import vacinafx.model.Aplicacao;

public class AplicacaoDAO extends GenericJDBCDAO<Aplicacao, Long> {
    public AplicacaoDAO(Connection conexao) {
        super(conexao);
    }

    private static final String FIND_ALL_QUERY = "SELECT codigo, data, pessoa, vacina, situacao FROM aplicacao WHERE situacao='ATIVO' ";
    private static final String FIND_BY_KEY_QUERY = FIND_ALL_QUERY + "AND codigo=? ";
    private static final String UPDATE_QUERY = "UPDATE aplicacao SET data=?, pessoa=?, vacina=?, situacao=? WHERE codigo=?";
    private static final String CREATE_QUERY = "INSERT INTO aplicacao (data, pessoa, vacina, situacao) VALUES (?, ?, ?, ?)";
    private static final String REMOVE_QUERY = "DELETE FROM aplicacao WHERE codigo=?";

    @Override
    protected Aplicacao toEntity(ResultSet resultSet) throws SQLException {
        Aplicacao aplicacao = new Aplicacao();
        aplicacao.setCodigo(resultSet.getLong("codigo"));
        aplicacao.setData(resultSet.getDate("nascimento").toLocalDate());

        aplicacao.getPessoa().setCodigo(resultSet.getLong("codigo"));
        aplicacao.getVacina().setCodigo(resultSet.getLong("codigo"));

        if (resultSet.getString("situacao").equals("ATIVO")) {
            aplicacao.setSituacao(Situacao.ATIVO);
        } else {
            aplicacao.setSituacao(Situacao.INATIVO);
        }
        return aplicacao;
    }

    @Override
    protected void addParameters(PreparedStatement resultSet, Aplicacao entity) throws SQLException {
        resultSet.setDate(1, java.sql.Date.valueOf(entity.getData()));
        resultSet.setLong(2, entity.getPessoa().getCodigo());
        resultSet.setLong(3, entity.getVacina().getCodigo());
        if (entity.getCodigo() != null) {
            resultSet.setLong(4, entity.getCodigo());
        }
    }

    @Override
    protected String findByKeyQuery() {
        return FIND_BY_KEY_QUERY;
    }

    @Override
    protected String findAllQuery() {
        return FIND_ALL_QUERY;
    }

    @Override
    protected String updateQuery() {
        return UPDATE_QUERY;
    }

    @Override
    protected String createQuery() {
        return CREATE_QUERY;
    }

    @Override
    protected String removeQuery() {
        return REMOVE_QUERY;
    }

    public void createAplicacao(Aplicacao aplicacao) {
        try {
            PreparedStatement statement = connection.prepareStatement(CREATE_QUERY);
            statement.setDate(1, java.sql.Date.valueOf(aplicacao.getData()));
            statement.setLong(2, aplicacao.getPessoa().getCodigo());
            statement.setLong(3, aplicacao.getVacina().getCodigo());
            statement.setString(4, aplicacao.getSituacao().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            showSQLException(e);
        }
    }

    @Override
    protected void setKeyInStatementFromEntity(PreparedStatement statement, Aplicacao entity) throws SQLException {
        statement.setLong(1, entity.getCodigo());
    }

    @Override
    protected void setKeyInStatement(PreparedStatement statement, Long key) throws SQLException {
        statement.setLong(1, key);
    }

    @Override
    protected void setKeyInEntity(ResultSet rs, Aplicacao entity) throws SQLException {
        entity.setCodigo(rs.getLong(1));
    }
}
