using System.ComponentModel;
using System.Drawing.Drawing2D;

namespace ec.edu.monster.Vista.Controles;

/// <summary>
/// Botón con esquinas redondeadas y colores para los estados normal, mouse encima y presionado.
/// Aparece en el Cuadro de herramientas de Visual Studio después de compilar el proyecto.
/// </summary>
public class BotonRedondeado : Button
{
    private bool mouseEncima;
    private bool presionado;
    private int radioBorde = 10;
    private Color colorHover = Color.FromArgb(0, 99, 177);
    private Color colorPresionado = Color.FromArgb(0, 62, 110);
    private Color colorBorde = Color.Empty;

    public BotonRedondeado()
    {
        SetStyle(ControlStyles.UserPaint | ControlStyles.AllPaintingInWmPaint
                | ControlStyles.OptimizedDoubleBuffer | ControlStyles.ResizeRedraw, true);
        FlatStyle = FlatStyle.Flat;
        FlatAppearance.BorderSize = 0;
        Cursor = Cursors.Hand;
        BackColor = Color.FromArgb(0, 84, 147);
        ForeColor = Color.White;
    }

    [Category("Apariencia redondeada")]
    [Description("Radio de las esquinas en píxeles.")]
    [DefaultValue(10)]
    public int RadioBorde
    {
        get => radioBorde;
        set { radioBorde = Math.Max(0, value); Invalidate(); }
    }

    [Category("Apariencia redondeada")]
    [Description("Color de fondo cuando el mouse está encima del botón.")]
    [DefaultValue(typeof(Color), "0, 99, 177")]
    public Color ColorHover
    {
        get => colorHover;
        set { colorHover = value; Invalidate(); }
    }

    [Category("Apariencia redondeada")]
    [Description("Color de fondo mientras el botón está presionado.")]
    [DefaultValue(typeof(Color), "0, 62, 110")]
    public Color ColorPresionado
    {
        get => colorPresionado;
        set { colorPresionado = value; Invalidate(); }
    }

    [Category("Apariencia redondeada")]
    [Description("Color del contorno. Vacío para no dibujar contorno.")]
    [DefaultValue(typeof(Color), "")]
    public Color ColorBorde
    {
        get => colorBorde;
        set { colorBorde = value; Invalidate(); }
    }

    protected override void OnMouseEnter(EventArgs e)
    {
        mouseEncima = true;
        Invalidate();
        base.OnMouseEnter(e);
    }

    protected override void OnMouseLeave(EventArgs e)
    {
        mouseEncima = false;
        presionado = false;
        Invalidate();
        base.OnMouseLeave(e);
    }

    protected override void OnMouseDown(MouseEventArgs e)
    {
        presionado = true;
        Invalidate();
        base.OnMouseDown(e);
    }

    protected override void OnMouseUp(MouseEventArgs e)
    {
        presionado = false;
        Invalidate();
        base.OnMouseUp(e);
    }

    protected override void OnEnabledChanged(EventArgs e)
    {
        Invalidate();
        base.OnEnabledChanged(e);
    }

    protected override void OnPaint(PaintEventArgs e)
    {
        Graphics g = e.Graphics;
        g.SmoothingMode = SmoothingMode.AntiAlias;
        g.Clear(Parent?.BackColor ?? SystemColors.Control);

        Color fondo = !Enabled ? Color.FromArgb(206, 212, 218)
                : presionado ? ColorPresionado
                : mouseEncima ? ColorHover
                : BackColor;

        var area = new Rectangle(0, 0, Width - 1, Height - 1);
        using (GraphicsPath ruta = CrearRutaRedondeada(area, RadioBorde))
        {
            using (var brocha = new SolidBrush(fondo))
            {
                g.FillPath(brocha, ruta);
            }
            if (!ColorBorde.IsEmpty && ColorBorde.A > 0)
            {
                using var lapiz = new Pen(Enabled ? ColorBorde : Color.FromArgb(206, 212, 218), 1.5f);
                g.DrawPath(lapiz, ruta);
            }
        }

        Color colorTexto = Enabled ? ForeColor : Color.White;
        TextRenderer.DrawText(g, Text, Font, ClientRectangle, colorTexto,
                TextFormatFlags.HorizontalCenter | TextFormatFlags.VerticalCenter
                | TextFormatFlags.SingleLine | TextFormatFlags.EndEllipsis);

        if (Focused && ShowFocusCues)
        {
            var areaFoco = Rectangle.Inflate(ClientRectangle, -4, -4);
            ControlPaint.DrawFocusRectangle(g, areaFoco, colorTexto, fondo);
        }
    }

    private static GraphicsPath CrearRutaRedondeada(Rectangle area, int radio)
    {
        var ruta = new GraphicsPath();
        int diametro = Math.Min(radio * 2, Math.Min(area.Width, area.Height));

        if (diametro <= 0)
        {
            ruta.AddRectangle(area);
            return ruta;
        }

        var esquina = new Rectangle(area.Location, new Size(diametro, diametro));
        ruta.AddArc(esquina, 180, 90);
        esquina.X = area.Right - diametro;
        ruta.AddArc(esquina, 270, 90);
        esquina.Y = area.Bottom - diametro;
        ruta.AddArc(esquina, 0, 90);
        esquina.X = area.Left;
        ruta.AddArc(esquina, 90, 90);
        ruta.CloseFigure();
        return ruta;
    }
}
