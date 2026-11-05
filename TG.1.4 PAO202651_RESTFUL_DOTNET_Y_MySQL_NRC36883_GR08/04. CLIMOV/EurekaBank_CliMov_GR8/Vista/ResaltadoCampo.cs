namespace ec.edu.monster.Vista;

/// <summary>
/// Pinta de azul el borde de un campo mientras el usuario escribe en él, y lo vuelve gris al salir.
/// </summary>
public static class ResaltadoCampo
{
    private static readonly Color ColorNormal = Color.FromArgb("#CED4DA");
    private static readonly Color ColorFoco = Color.FromArgb("#005493");
    private static readonly Color ColorError = Color.FromArgb("#C00000");

    public static void Aplicar(VisualElement campo, Border borde)
    {
        campo.Focused += (_, _) =>
        {
            borde.Stroke = ColorFoco;
            borde.StrokeThickness = 2;
        };
        campo.Unfocused += (_, _) =>
        {
            borde.Stroke = ColorNormal;
            borde.StrokeThickness = 1.5;
        };
    }

    public static void MarcarError(Border borde)
    {
        borde.Stroke = ColorError;
        borde.StrokeThickness = 2;
    }

    public static void QuitarError(Border borde)
    {
        borde.Stroke = ColorNormal;
        borde.StrokeThickness = 1.5;
    }
}
