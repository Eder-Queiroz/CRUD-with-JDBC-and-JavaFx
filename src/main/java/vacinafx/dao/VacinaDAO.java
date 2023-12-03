package vacinafx.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import vacinafx.dao.core.GenericJDBCDAO;
import vacinafx.model.Situacao;
import vacinafx.model.Vacina;

public class VacinaDAO extends GenericJDBCDAO<Vacina, Long> {

    public VacinaDAO(Connection conexao) {
        super(conexao);
    }

    private static final String FIND_ALL_QUERY = "SELECT codigo, nome, descricao, situacao FROM vacina WHERE situacao='ATIVO' ";
    private static final String FIND_BY_KEY_QUERY = FIND_ALL_QUERY + "AND codigo=? ";
    private static final String FIND_BY_NAME_LIKE_QUERY = FIND_ALL_QUERY + "AND upper(nome) like upper(?)";
    private static final String UPDATE_QUERY = "UPDATE vacina SET nome=?, descricao=?, situacao=? WHERE codigo=?";
    private static final String CREATE_QUERY = "INSERT INTO vacina (nome, descricao, situacao) VALUES (?, ?, ?)";
    private static final String REMOVE_QUERY = "DELETE FROM vacina WHERE codigo=?";
    private static final String FIND_BY_DESCRICAO = FIND_ALL_QUERY + "AND descricao=?";
    private static final String FIND_BY_FILTER = FIND_ALL_QUERY + "AND codigo=? AND nome=? AND descricao=?";

    @Override
    protected Vacina toEntity(ResultSet resultSet) throws SQLException {
        Vacina vacina = new Vacina();
        vacina.setCodigo(resultSet.getLong("codigo"));
        vacina.setNome(resultSet.getString("nome"));
        vacina.setDescricao(resultSet.getString("descricao"));
        if (resultSet.getString("situacao").equals("ATIVO")) {
            vacina.setSituacao(Situacao.ATIVO);
        } else {
            vacina.setSituacao(Situacao.INATIVO);
        }
        return vacina;
    }

    @Override
    protected void addParameters(PreparedStatement resultSet, Vacina entity) throws SQLException {
        resultSet.setString(1, entity.getNome());
        resultSet.setString(2, entity.getDescricao());
        resultSet.setString(3, entity.getSituacao().toString());
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

    public List<Vacina> findByNameLike(String nome) {
        try {
            PreparedStatement statement = connection.prepareStatement(FIND_BY_NAME_LIKE_QUERY);
            statement.setString(1, "%" + nome + "%");
            ResultSet resultSet = statement.executeQuery();
            return toEntityList(resultSet);
        } catch (SQLException e) {
            showSQLException(e);
        }
        return new ArrayList<Vacina>();
    }

    public List<Vacina> findAll() {
        try {
            PreparedStatement statement = connection.prepareStatement(FIND_ALL_QUERY);
            ResultSet resultSet = statement.executeQuery();
            return toEntityList(resultSet);
        } catch (SQLException e) {
            showSQLException(e);
        }

        return new ArrayList<Vacina>();
    }

    public List<Vacina> findWithFilter(String codigo, String nome, String descricao) {
        try {
            PreparedStatement statement;

            if (!codigo.isEmpty() && !nome.isEmpty() && !descricao.isEmpty()) {
                System.out.println("\n\n filter \n\n");
                statement = connection.prepareStatement(FIND_BY_FILTER);
                statement.setLong(1, Long.parseLong(codigo));
                statement.setString(2, nome);
                statement.setString(3, descricao);
            } else if (!codigo.isEmpty()) {
                System.out.println("\n\n codigo \n\n");
                statement = connection.prepareStatement(FIND_BY_KEY_QUERY);
                statement.setLong(1, Long.parseLong(codigo));
            } else if (!nome.isEmpty()) {
                System.out.println("\n\n nome \n\n");
                statement = connection.prepareStatement(FIND_BY_NAME_LIKE_QUERY);
                statement.setString(1, nome);
            } else if (!descricao.isEmpty()) {
                System.out.println("\n\n descricao \n\n");
                statement = connection.prepareStatement(FIND_BY_DESCRICAO);
                statement.setString(1, descricao);
            } else {
                System.out.println("\n\n all \n\n");
                statement = connection.prepareStatement(FIND_ALL_QUERY);
            }

            ResultSet resultSet = statement.executeQuery();
            return toEntityList(resultSet);
        } catch (SQLException e) {
            showSQLException(e);
        }

        return new ArrayList<Vacina>();
    }

    public void updateVacina(Vacina vacina) {
        try {
            PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY);
            statement.setString(1, vacina.getNome());
            statement.setString(2, vacina.getDescricao());
            statement.setString(3, vacina.getSituacao().toString());
            statement.setLong(4, vacina.getCodigo());
            statement.executeUpdate();
        } catch (SQLException e) {
            showSQLException(e);
        }
    }

    public void createVacina(Vacina vacina) {
        try {
            PreparedStatement statement = connection.prepareStatement(CREATE_QUERY);
            statement.setString(1, vacina.getNome());
            statement.setString(2, vacina.getDescricao());
            statement.setString(3, vacina.getSituacao().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            showSQLException(e);
        }
    }

    public void removeVacina(Vacina vacina) {
        try {
            PreparedStatement statement = connection.prepareStatement(REMOVE_QUERY);
            statement.setLong(1, vacina.getCodigo());
            statement.executeUpdate();
        } catch (SQLException e) {
            showSQLException(e);
        }
    }

    @Override
    protected void setKeyInStatementFromEntity(PreparedStatement statement, Vacina entity) throws SQLException {
        statement.setLong(1, entity.getCodigo());
    }

    @Override
    protected void setKeyInStatement(PreparedStatement statement, Long key) throws SQLException {
        statement.setLong(1, key);
    }

    @Override
    protected void setKeyInEntity(ResultSet rs, Vacina entity) throws SQLException {
        entity.setCodigo(rs.getLong(1));
    }

}