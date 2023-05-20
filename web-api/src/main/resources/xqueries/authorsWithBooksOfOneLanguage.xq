declare variable $language as xs:string external;

for $author in //author
where $author/books/book/language = $language
return $author
