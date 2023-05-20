<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes"/>
    <xsl:template match="/">
        <html>
            <head>
                <title>The Author</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous" />
            </head>
            <body>
                <xsl:for-each select="authors/author">
                    <div class="container">
                        <hr></hr>
                        <h1>Author Information</h1>

                        <div class="row">
                            <div class="col-md-9">
                                <p><b>ID: </b><xsl:value-of select="id"/></p>
                                <p><b>Complete Name: </b><xsl:value-of select="fullName"/></p>
                                <p><b>Birthdate: </b><xsl:value-of select="birthDateString"/></p>
                                <p><b>Death date: </b><xsl:value-of select="deathDateString"/></p>
                                <p><b>Nacionality: </b><xsl:value-of select="nationality"/></p>
                                <p>
                                   <a href="{wikipediaUrl}" target="_blank"> <button class="btn btn-primary">View Source Wikipedia</button></a>
                                </p>
                            </div>
                            <div class="col-md-3">
                                <img src="{coverImageUrl}" style="height:300px;width:200px;"></img>
                            </div>
                        </div>
                        <br/>
                        <div class="row">
                            <div class="col">
                                <p><b>Biography: </b><xsl:value-of select="biography"/></p>
                            </div>
                        </div>

                        <h1>Some of the books</h1>
                        <ul>
                            <xsl:for-each select="books/book">
                             <div class="row" style="margin-bottom:15px;">
                                 <div class="col-md-9">
                                     <li>
                                         <b>Title: </b><xsl:value-of select="title"/><br/>
                                         <b>ISBN: </b><xsl:value-of select="isbn"/><br/>
                                         <b>Publisher: </b><xsl:value-of select="publisher"/><br/>
                                         <b>Price: </b><xsl:value-of select="price"/>
                                        <p> <a href="{bertrandUrl}" target="_blank"><button class="btn btn-primary">View the book</button></a> </p>
                                     </li>
                                 </div>
                                 <div class="col-md-3">
                                     <img src="{coverImageUrl}"></img>
                                 </div>
                             </div>
                            </xsl:for-each>
                        </ul>
                    </div>
                </xsl:for-each>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
