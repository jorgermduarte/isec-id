declare variable $numberOfBooks as xs:integer external;

let $orderedBooks :=
  for $book in //book
  order by xs:decimal($book/price) descending
  return $book

for $book in $orderedBooks[position() <= $numberOfBooks]
return $book
