declare variable $minBooks as xs:integer external;

for $author in //author
where count($author/books/book) >= $minBooks
return $author
