
select photoid from photoscategories pc inner join 
categories c on pc.categoryid = c.categoryid and c.name = 'viajes';



select p.* from photos p, photoscategories pc where p.photoid = pc.photoid and pc.photoid =
 ( select pc.photoid from photoscategories pc, categories c where pc.categoryid = c.categoryid and c.name = 'viajes');