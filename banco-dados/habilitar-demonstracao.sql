-- Execute manualmente no SSMS para tabelas já criadas. Preserva os dados.
USE [COMPARADOR_PRECO];
GO

DECLARE @comando nvarchar(max) = N'';
SELECT @comando = @comando + N'ALTER TABLE dbo.BUSCAS DROP CONSTRAINT '
    + QUOTENAME(name) + N';'
FROM sys.check_constraints
WHERE parent_object_id = OBJECT_ID(N'dbo.BUSCAS')
  AND definition LIKE N'%modo%';

IF LEN(@comando) > 0 EXEC sys.sp_executesql @comando;

ALTER TABLE dbo.BUSCAS WITH CHECK ADD CONSTRAINT CK_BUSCAS_MODO
CHECK (modo IN ('SINCRONO', 'ASSINCRONO', 'ASSINCRONO_SIMULADO'));
GO
