<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html"/>
    <xsl:template match="/">
        <html>
            <head>
                <title>Books</title>
            </head>
            <body>
                <table border="1">
                    <tr>
                        <th>ID</th>
                        <th>Title</th>
                        <th>Author</th>
                        <th>ISBN</th>
                        <th>Publication Date</th>
                        <th>Publisher</th>
                        <th>Language</th>
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
            <td><xsl:value-of select="author"/></td>
            <td><xsl:value-of select="isbn"/></td>
            <td><xsl:value-of select="publicationDate"/></td>
            <td><xsl:value-of select="publisher"/></td>
            <td><xsl:value-of select="language"/></td>
        </tr>
    </xsl:template>
</xsl:stylesheet>
