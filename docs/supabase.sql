-- tabla de guias educativas para la app myciclo
-- esto se pega en el SQL Editor de supabase y se ejecuta una vez
-- supabase publica la tabla sola en /rest/v1/guias, no hay que escribir backend

create table if not exists guias (
  id        bigint generated always as identity primary key,
  titulo    text not null,
  contenido text not null,
  categoria text not null
);

-- las columnas se llaman igual que los campos de Guia.kt
-- si se les cambia el nombre hay que cambiar tambien el data class

-- row level security prendido: sin politicas nadie puede leer ni escribir
-- supabase lo pide asi, si no la tabla queda abierta para cualquiera
alter table guias enable row level security;

-- la app solo lee, y el contenido educativo es publico, por eso basta un select para anon
-- no creamos politica de insert ni de update: las guias las carga el perfil
-- administrador desde el panel de supabase, que es el perfil secundario del caso
create policy "lectura publica de guias"
  on guias
  for select
  to anon
  using (true);

-- un par de filas de ejemplo para probar que la app las trae
-- las demas se cargan desde el panel, o se copian de ContenidoLocalRepository
insert into guias (titulo, contenido, categoria) values
  ('Como usar EVA en 4 pasos',
   'Tomar la muestra, dejarla secar, observar el patron con el aumento 50x y registrar el resultado en la app.',
   'Manual'),
  ('EVA no es un metodo anticonceptivo',
   'EVA es una herramienta de autoconocimiento y educacion. No reemplaza una evaluacion clinica.',
   'Preguntas frecuentes');

-- para comprobar que quedo publicada, desde el navegador o con curl:
-- https://<proyecto>.supabase.co/rest/v1/guias?select=*&apikey=<anon key>
