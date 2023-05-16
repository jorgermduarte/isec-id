<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html"/>
    <xsl:template match="/">
        <html>
            <head>
                <title>Books</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous" />
            </head>
            <body>
                <h1>Livros</h1>
                <table border="1" class="table table-striped">
                    <tr>
                        <th scope="col">ID</th>
                        <th scope="col">Title</th>
                        <th scope="col">Author</th>
                        <th scope="col">ISBN</th>
                        <th scope="col">Publication Date</th>
                        <th scope="col">Publisher</th>
                        <th scope="col">Language</th>
                        <th scope="col">Image</th>
                    </tr>
                    <xsl:apply-templates select="books/book"/>
                </table>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="book">
        <tr>
            <td><xsl:value-of select="id"/></td>
            <td><xsl:value-of select="title"/></td>
            <td><xsl:value-of select="authorId"/></td>
            <td><xsl:value-of select="isbn"/></td>
            <td><xsl:value-of select="publicationDateString"/></td>
            <td><xsl:value-of select="publisher"/></td>
            <td><xsl:value-of select="language"/></td>
            <td>
                <img src="{coverImageUrl}" alt="{title}"/>
            </td>
        </tr>
    </xsl:template>
</xsl:stylesheet>
