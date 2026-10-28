-- Contenido educativo público
create table if not exists guias (
  id bigint generated always as identity primary key,
  titulo text not null,
  contenido text not null,
  categoria text not null
);
alter table guias enable row level security;
drop policy if exists "lectura publica de guias" on guias;
create policy "lectura publica de guias" on guias for select to anon, authenticated using (true);

-- Datos privados: cada fila pertenece a la usuaria autenticada.
create table if not exists registros_ciclo (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  fecha date not null,
  inicio_periodo date not null,
  nivel text not null check (nivel in ('ALTO', 'MEDIO', 'NULO')),
  notas text not null default '',
  foto_path text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  unique (user_id, fecha)
);

alter table registros_ciclo enable row level security;
drop policy if exists "registros propios select" on registros_ciclo;
drop policy if exists "registros propios insert" on registros_ciclo;
drop policy if exists "registros propios update" on registros_ciclo;
drop policy if exists "registros propios delete" on registros_ciclo;
create policy "registros propios select" on registros_ciclo for select to authenticated using (auth.uid() = user_id);
create policy "registros propios insert" on registros_ciclo for insert to authenticated with check (auth.uid() = user_id);
create policy "registros propios update" on registros_ciclo for update to authenticated using (auth.uid() = user_id) with check (auth.uid() = user_id);
create policy "registros propios delete" on registros_ciclo for delete to authenticated using (auth.uid() = user_id);

-- Las fotos deben ir a un bucket privado con políticas equivalentes a auth.uid().
-- Nunca expongas la service_role en la app Android.
