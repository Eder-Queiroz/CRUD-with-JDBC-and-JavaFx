package vacinafx.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import vacinafx.dao.core.GenericJDBCDAO;
import vacinafx.model.Pessoa;
import vacinafx.model.Situacao;

public class PessoaDAO extends GenericJDBCDAO<Pessoa, Long> {
    public PessoaDAO(Connection conexao) {
        super(conexao);
    }

    private static final String FIND_ALL_QUERY = "SELECT * FROM pessoa WHERE situacao='ATIVO' ";
    private static final String FIND_BY_KEY_QUERY = FIND_ALL_QUERY + "AND codigo=? ";
    private static final String FIND_BY_NAME_LIKE_QUERY = FIND_ALL_QUERY + "AND upper(nome) like upper(?)";
    private static final String UPDATE_QUERY = "UPDATE pessoa SET nome=?, cpf=?, nascimento=? situacao=? WHERE codigo=?";
    private static final String CREATE_QUERY = "INSERT INTO pessoa (nome, cpf, nascimento, situacao) VALUES (?, ?, ?, ?)";
    private static final String REMOVE_QUERY = "DELETE FROM pessoa WHERE codigo=?";
    private static final String FIND_BY_CPF = FIND_ALL_QUERY + "AND cpf=?";
    private static final String FIND_BY_DATE = FIND_ALL_QUERY + "AND nascimento BETWEEN ? AND ?";
    private static final String FIND_BY_FILTER = FIND_ALL_QUERY
            + "AND codigo=? AND nome=? AND cpf=? AND nascimento=?";

    @Override
    protected Pessoa toEntity(ResultSet resultSet) throws SQLException {
        Pessoa pessoa = new Pessoa();
        pessoa.setCodigo(resultSet.getLong("codigo"));
        pessoa.setNome(resultSet.getString("nome"));
        pessoa.setCpf(resultSet.getString("cpf"));
        pessoa.setNascimento(resultSet.getDate("nascimento").toLocalDate());
        if (resultSet.getString("situacao").equals("ATIVO")) {
            pessoa.setSituacao(Situacao.ATIVO);
        } else {
            pessoa.setSituacao(Situacao.INATIVO);
        }
        return pessoa;
    }

    @Override
    protected void addParameters(PreparedStatement resultSet, Pessoa entity) throws SQLException {
        resultSet.setString(1, entity.getNome());
        resultSet.setString(2, entity.getCpf());
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

    public List<Pessoa> findByNameLike(String nome) {
        try {
            PreparedStatement statement = connection.prepareStatement(FIND_BY_NAME_LIKE_QUERY);
            statement.setString(1, "%" + nome + "%");
            ResultSet resultSet = statement.executeQuery();
            return toEntityList(resultSet);
        } catch (SQLException e) {
            showSQLException(e);
        }
        return new ArrayList<Pessoa>();
    }

    public List<Pessoa> findAll() {
        try {
            PreparedStatement statement = connection.prepareStatement(FIND_ALL_QUERY);
            ResultSet resultSet = statement.executeQuery();
            return toEntityList(resultSet);
        } catch (SQLException e) {
            showSQLException(e);
        }

        return new ArrayList<Pessoa>();
    }

    public List<Pessoa> findWithFilter(String codigo, String nome, String cpf, LocalDate ApartirData,
            LocalDate AteData) {
        try {
            PreparedStatement statement;

            if (!codigo.isEmpty() && !nome.isEmpty() && !cpf.isEmpty() && ApartirData != null && AteData != null) {
                System.out.println("\n\n filter \n\n");
                statement = connection.prepareStatement(FIND_BY_FILTER);
                statement.setLong(1, Long.parseLong(codigo));
                statement.setString(2, nome);
                statement.setString(3, cpf);
            } else if (!codigo.isEmpty()) {
                System.out.println("\n\n codigo \n\n");
                statement = connection.prepareStatement(FIND_BY_KEY_QUERY);
                statement.setLong(1, Long.parseLong(codigo));
            } else if (!nome.isEmpty()) {
                System.out.println("\n\n nome \n\n");
                statement = connection.prepareStatement(FIND_BY_NAME_LIKE_QUERY);
                statement.setString(1, nome);
            } else if (!cpf.isEmpty()) {
                System.out.println("\n\n descricao \n\n");
                statement = connection.prepareStatement(FIND_BY_CPF);
                statement.setString(1, cpf);
            } else if (ApartirData != null && AteData != null) {
                System.out.println("\n\n data \n\n");
                statement = connection.prepareStatement(FIND_BY_DATE);
                statement.setDate(1, java.sql.Date.valueOf(ApartirData));
                statement.setDate(2, java.sql.Date.valueOf(AteData));

                System.out.println("\n\n" + java.sql.Date.valueOf(ApartirData) + "\n\n");
                System.out.println("\n\n" + statement);
            } else {
                System.out.println("\n\n all \n\n");
                statement = connection.prepareStatement(FIND_ALL_QUERY);
            }

            ResultSet resultSet = statement.executeQuery();
            return toEntityList(resultSet);
        } catch (SQLException e) {
            showSQLException(e);
        }

        return new ArrayList<Pessoa>();
    }

    @Override
    protected void setKeyInStatementFromEntity(PreparedStatement statement, Pessoa entity) throws SQLException {
        statement.setLong(1, entity.getCodigo());
    }

    @Override
    protected void setKeyInStatement(PreparedStatement statement, Long key) throws SQLException {
        statement.setLong(1, key);
    }

    @Override
    protected void setKeyInEntity(ResultSet rs, Pessoa entity) throws SQLException {
        entity.setCodigo(rs.getLong(1));
    }

}
