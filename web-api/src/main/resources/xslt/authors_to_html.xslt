<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" indent="yes" encoding="UTF-8" doctype-system="about:legacy-compat"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>Autores</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous" />
            </head>
            <body>
                <h1>Autores</h1>
                <table border="1" class="table table-striped">
                    <tr>
                        <th scope="col">ID</th>
                        <th scope="col">Nome</th>
                        <th scope="col">Data de Nascimento</th>
                        <th scope="col">Data de Falecimento</th>
                    </tr>
                    <xsl:apply-templates select="authors/author"/>
                </table>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="author">
        <tr>
            <td><xsl:value-of select="id"/></td>
            <td><xsl:value-of select="fullName"/></td>
            <td><xsl:value-of select="birthDateString"/></td>
            <td><xsl:value-of select="deathDateString"/></td>
        </tr>
    </xsl:template>

</xsl:stylesheet>
