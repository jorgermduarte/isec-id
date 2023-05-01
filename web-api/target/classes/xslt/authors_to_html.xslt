<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" indent="yes" encoding="UTF-8" doctype-system="about:legacy-compat"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>Autores</title>
            </head>
            <body>
                <h1>Autores</h1>
                <table border="1">
                    <tr>
                        <th>Nome</th>
                        <th>Data de Nascimento</th>
                        <th>Data de Falecimento</th>
                    </tr>
                    <xsl:apply-templates select="authors/author"/>
                </table>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="author">
        <tr>
            <td><xsl:value-of select="fullName"/></td>
            <td><xsl:value-of select="birthDateString"/></td>
            <td><xsl:value-of select="deathDateString"/></td>
        </tr>
    </xsl:template>

</xsl:stylesheet>
