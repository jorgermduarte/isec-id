declare variable $maxPrice as xs:double external;

for $book in //book
where $book/price < $maxPrice
return $book
