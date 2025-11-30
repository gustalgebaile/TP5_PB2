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
                                <th>Gênero</th>
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
                                <td>%s</td>
                                <td>
                                    <a href="/biblioteca/edit/%d" class="btn btn-sm btn-warning">Editar</a>
                                    <form action="/biblioteca/delete/%d" method="post" style="display:inline;">
                                        <button type="submit" class="btn btn-sm btn-danger">Deletar</button>
                                    </form>
                                </td>
                            </tr>
                            """, book.getId(), book.getName(), book.getAutor(),
                    book.getCategory() != null ? book.getCategory() : "Não definido",
                    book.getId(), book.getId()));
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
        String category = (String) model.getOrDefault("category", "");

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
                                <div class="mb-3">
                                    <label for="category" class="form-label">Gênero</label>
                                    <select class="form-select" id="category" name="category" required>
                                        <option value="">Selecione um Gênero</option>
                                        <option value="Ficção" %s>Ficção</option>
                                        <option value="Não-Ficção" %s>Não-Ficção</option>
                                        <option value="Romance" %s>Romance</option>
                                        <option value="Mistério" %s>Mistério</option>
                                        <option value="Fantasia" %s>Fantasia</option>
                                        <option value="Terror" %s>Terror</option>
                                        <option value="Ciência Ficção" %s>Ciência Ficção</option>
                                        <option value="Biografia" %s>Biografia</option>
                                        <option value="Poesia" %s>Poesia</option>
                                        <option value="Distopia" %s>Distopia</option>
                                        <option value="Infantil" %s>Infantil</option>
                                        <option value="Épico" %s>Épico</option>
                                    </select>
                                </div>
                                <button type="submit" class="btn btn-success">Salvar</button>
                                <a href="/biblioteca" class="btn btn-secondary">Cancelar</a>
                            </form>
                        </body>
                        </html>
                        """, title, title, action, name, autor,
                category.equals("Ficção") ? "selected" : "",
                category.equals("Não-Ficção") ? "selected" : "",
                category.equals("Romance") ? "selected" : "",
                category.equals("Mistério") ? "selected" : "",
                category.equals("Fantasia") ? "selected" : "",
                category.equals("Terror") ? "selected" : "",
                category.equals("Ciência Ficção") ? "selected" : "",
                category.equals("Biografia") ? "selected" : "",
                category.equals("Poesia") ? "selected" : "",
                category.equals("Distopia") ? "selected" : "",
                category.equals("Infantil") ? "selected" : "",
                category.equals("Épico") ? "selected" : "");
    }
}
