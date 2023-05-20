declare variable $nationality as xs:string external;

for $author in //author
where $author/nationality = $nationality
return $author
