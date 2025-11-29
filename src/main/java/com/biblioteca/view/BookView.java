package com.biblioteca.view;


import com.biblioteca.model.Book;


import java.util.List;
import java.util.Map;

public class BookView {

    public static String renderList(List<Book> books) {
        StringBuilder html = new StringBuilder("""
                <!DOCTYPE html>
                <html lang="pt">
                <head>
                    <meta charset="UTF-8">
                    <title>Lista de Livros</title>
                    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
                </head>
                <body class="container mt-5">
                    <h1>Lista de Livros</h1>
                    <a href="/biblioteca/new" class="btn btn-primary mb-3">Adicionar Novo Livro</a>
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nome do Livro</th>
                                <th>Autor</th>
                                <th>Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                """);
        for (Book book : books) {
            html.append(String.format("""
                    <tr>
                        <td>%d</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>
                            <a href="/biblioteca/edit/%d" class="btn btn-sm btn-warning">Editar</a>
                            <form action="/biblioteca/delete/%d" method="post" style="display:inline;">
                                <button type="submit" class="btn btn-sm btn-danger">Deletar</button>
                            </form>
                        </td>
                    </tr>
                    """, book.getId(), book.getName(), book.getAutor(), book.getId(), book.getId()));
        }
        html.append("""
                        </tbody>
                    </table>
                </body>
                </html>
                """);
        return html.toString();
    }

    public static String renderForm(Map<String, Object> model) {
        Object id = model.get("id");
        String action = id != null ? "/biblioteca/edit/" + id : "/biblioteca";
        String title = id != null ? "Editar Livro" : "Novo Livro";
        String name = (String) model.getOrDefault("name", "");
        String autor = (String) model.getOrDefault("autor", "");

        return String.format("""
                <!DOCTYPE html>
                <html lang="pt">
                <head>
                    <meta charset="UTF-8">
                    <title>%s</title>
                    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
                </head>
                <body class="container mt-5">
                    <h1>%s</h1>
                    <form action="%s" method="post">
                        <div class="mb-3">
                            <label for="name" class="form-label">Nome do Livro</label>
                            <input type="text" class="form-control" id="name" name="name" value="%s" required>
                        </div>
                        <div class="mb-3">
                            <label for="autor" class="form-label">Autor</label>
                            <input type="text" class="form-control" id="autor" name="autor" value="%s" required>
                        </div>
                        <button type="submit" class="btn btn-success">Salvar</button>
                        <a href="/items" class="btn btn-secondary">Cancelar</a>
                    </form>
                </body>
                </html>
                """, title, title, action, name, autor);
    }
}
