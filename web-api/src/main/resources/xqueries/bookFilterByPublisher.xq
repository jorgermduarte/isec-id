declare variable $publisher as xs:string external;

for $book in //book
where $book/publisher = $publisher
return $book
