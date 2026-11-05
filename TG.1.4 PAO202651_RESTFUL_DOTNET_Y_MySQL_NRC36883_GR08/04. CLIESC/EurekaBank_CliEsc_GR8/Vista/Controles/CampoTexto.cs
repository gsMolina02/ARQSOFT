using System.ComponentModel;
using System.Diagnostics.CodeAnalysis;
using System.Drawing.Drawing2D;

namespace ec.edu.monster.Vista.Controles;

/// <summary>
/// Cuadro de texto con bordes redondeados, margen interno y borde resaltado al recibir el foco.
/// En modo contraseña muestra un botón con un ojo para ver u ocultar lo escrito.
/// Con ConLista muestra una flecha que pide abrir la lista de opciones (evento ListaSolicitada).
/// Aparece en el Cuadro de herramientas de Visual Studio después de compilar el proyecto.
/// </summary>
[DefaultEvent(nameof(TextChanged))]
public class CampoTexto : Control
{
    // Glifos de la fuente "Segoe MDL2 Assets" (incluida en Windows 10 y 11)
    private const string GlifoMostrar = "";
    private const string GlifoOcultar = "";
    private const string GlifoLista = "\uE70D";
    private const int MargenHorizontal = 12;
    private const int AnchoBotonOjo = 36;

    private readonly TextBox cajaTexto = new();
    private readonly Label botonOjo = new();
    private readonly Label botonLista = new();
    private bool esContrasena;
    private bool conLista;
    private bool contrasenaVisible;
    private bool mouseEncima;
    private int radioBorde = 10;
    private Color colorBorde = Color.FromArgb(206, 212, 218);
    private Color colorBordeFoco = Color.FromArgb(0, 84, 147);

    public CampoTexto()
    {
        SetStyle(ControlStyles.UserPaint | ControlStyles.AllPaintingInWmPaint
                | ControlStyles.OptimizedDoubleBuffer | ControlStyles.ResizeRedraw, true);
        BackColor = Color.White;
        ForeColor = Color.FromArgb(33, 37, 41);
        Cursor = Cursors.IBeam;
        Size = new Size(300, 44);

        cajaTexto.BorderStyle = BorderStyle.None;
        cajaTexto.BackColor = BackColor;
        cajaTexto.ForeColor = ForeColor;
        cajaTexto.TextChanged += (_, e) => OnTextChanged(e);
        cajaTexto.KeyPress += (_, e) => OnKeyPress(e);
        cajaTexto.KeyDown += (_, e) => CajaTexto_KeyDown(e);
        cajaTexto.GotFocus += (_, _) => Invalidate();
        cajaTexto.LostFocus += (_, _) => Invalidate();
        cajaTexto.MouseEnter += (_, _) => CambiarMouseEncima(true);
        cajaTexto.MouseLeave += (_, _) => CambiarMouseEncima(false);

        botonOjo.Font = new Font("Segoe MDL2 Assets", 11F);
        botonOjo.ForeColor = Color.FromArgb(108, 117, 125);
        botonOjo.BackColor = BackColor;
        botonOjo.TextAlign = ContentAlignment.MiddleCenter;
        botonOjo.Cursor = Cursors.Hand;
        botonOjo.Visible = false;
        botonOjo.Text = GlifoMostrar;
        botonOjo.Click += (_, _) => AlternarContrasenaVisible();
        botonOjo.MouseEnter += (_, _) => { botonOjo.ForeColor = colorBordeFoco; CambiarMouseEncima(true); };
        botonOjo.MouseLeave += (_, _) => { botonOjo.ForeColor = Color.FromArgb(108, 117, 125); CambiarMouseEncima(false); };

        botonLista.Font = new Font("Segoe MDL2 Assets", 11F);
        botonLista.ForeColor = Color.FromArgb(108, 117, 125);
        botonLista.BackColor = BackColor;
        botonLista.TextAlign = ContentAlignment.MiddleCenter;
        botonLista.Cursor = Cursors.Hand;
        botonLista.Visible = false;
        botonLista.Text = GlifoLista;
        botonLista.Click += (_, _) => SolicitarLista();
        botonLista.MouseEnter += (_, _) => { botonLista.ForeColor = colorBordeFoco; CambiarMouseEncima(true); };
        botonLista.MouseLeave += (_, _) => { botonLista.ForeColor = Color.FromArgb(108, 117, 125); CambiarMouseEncima(false); };

        Controls.Add(cajaTexto);
        Controls.Add(botonOjo);
        Controls.Add(botonLista);
    }

    /// <summary>Se pulsó la flecha (o Alt+Abajo / F4): el formulario muestra la lista de opciones.</summary>
    [Category("Acción")]
    [Description("Se pide abrir la lista de opciones del campo.")]
    public event EventHandler? ListaSolicitada;

    [Browsable(true)]
    [EditorBrowsable(EditorBrowsableState.Always)]
    [DesignerSerializationVisibility(DesignerSerializationVisibility.Visible)]
    [AllowNull]
    public override string Text
    {
        get => cajaTexto.Text;
        set => cajaTexto.Text = value ?? string.Empty;
    }

    [Category("Comportamiento")]
    [Description("Texto de ayuda que se muestra mientras el campo está vacío.")]
    [DefaultValue("")]
    public string PlaceholderText
    {
        get => cajaTexto.PlaceholderText;
        set => cajaTexto.PlaceholderText = value ?? string.Empty;
    }

    [Category("Comportamiento")]
    [Description("Cantidad máxima de caracteres.")]
    [DefaultValue(32767)]
    public int MaxLength
    {
        get => cajaTexto.MaxLength;
        set => cajaTexto.MaxLength = value;
    }

    [Category("Comportamiento")]
    [Description("Oculta lo escrito y muestra el botón para ver la contraseña.")]
    [DefaultValue(false)]
    public bool EsContrasena
    {
        get => esContrasena;
        set
        {
            esContrasena = value;
            contrasenaVisible = false;
            ActualizarModoContrasena();
        }
    }

    [Category("Comportamiento")]
    [Description("Muestra una flecha a la derecha para elegir el valor de una lista.")]
    [DefaultValue(false)]
    public bool ConLista
    {
        get => conLista;
        set
        {
            conLista = value;
            botonLista.Visible = value && !esContrasena;
            PerformLayout();
        }
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
    [Description("Color del borde en estado normal.")]
    [DefaultValue(typeof(Color), "206, 212, 218")]
    public Color ColorBorde
    {
        get => colorBorde;
        set { colorBorde = value; Invalidate(); }
    }

    [Category("Apariencia redondeada")]
    [Description("Color del borde cuando el campo tiene el foco.")]
    [DefaultValue(typeof(Color), "0, 84, 147")]
    public Color ColorBordeFoco
    {
        get => colorBordeFoco;
        set { colorBordeFoco = value; Invalidate(); }
    }

    [Browsable(false)]
    [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
    public int SelectionStart
    {
        get => cajaTexto.SelectionStart;
        set => cajaTexto.SelectionStart = value;
    }

    [Browsable(false)]
    [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
    public int SelectionLength
    {
        get => cajaTexto.SelectionLength;
        set => cajaTexto.SelectionLength = value;
    }

    public void SelectAll() => cajaTexto.SelectAll();

    public void Clear() => cajaTexto.Clear();

    public new bool Focus() => cajaTexto.Focus();

    protected override void OnGotFocus(EventArgs e)
    {
        base.OnGotFocus(e);
        cajaTexto.Focus();
    }

    protected override void OnClick(EventArgs e)
    {
        base.OnClick(e);
        cajaTexto.Focus();
    }

    protected override void OnMouseEnter(EventArgs e)
    {
        base.OnMouseEnter(e);
        CambiarMouseEncima(true);
    }

    protected override void OnMouseLeave(EventArgs e)
    {
        base.OnMouseLeave(e);
        CambiarMouseEncima(false);
    }

    protected override void OnFontChanged(EventArgs e)
    {
        base.OnFontChanged(e);
        cajaTexto.Font = Font;
        PerformLayout();
    }

    protected override void OnForeColorChanged(EventArgs e)
    {
        base.OnForeColorChanged(e);
        cajaTexto.ForeColor = ForeColor;
    }

    protected override void OnBackColorChanged(EventArgs e)
    {
        base.OnBackColorChanged(e);
        cajaTexto.BackColor = BackColor;
        botonOjo.BackColor = BackColor;
        botonLista.BackColor = BackColor;
    }

    protected override void OnEnabledChanged(EventArgs e)
    {
        base.OnEnabledChanged(e);
        Color fondo = Enabled ? BackColor : Color.FromArgb(248, 249, 250);
        cajaTexto.BackColor = fondo;
        botonOjo.BackColor = fondo;
        botonLista.BackColor = fondo;
        Invalidate();
    }

    // Texto centrado verticalmente con margen a los lados; el ojo o la flecha ocupan el extremo derecho
    protected override void OnLayout(LayoutEventArgs levent)
    {
        base.OnLayout(levent);

        int anchoOjo = esContrasena || conLista ? AnchoBotonOjo : 0;
        int altoTexto = cajaTexto.PreferredHeight;
        cajaTexto.SetBounds(MargenHorizontal, (Height - altoTexto) / 2,
                Math.Max(0, Width - MargenHorizontal * 2 - anchoOjo), altoTexto);
        botonOjo.SetBounds(Width - AnchoBotonOjo - 6, 4, AnchoBotonOjo, Math.Max(0, Height - 8));
        botonLista.Bounds = botonOjo.Bounds;
    }

    protected override void OnPaint(PaintEventArgs e)
    {
        Graphics g = e.Graphics;
        g.SmoothingMode = SmoothingMode.AntiAlias;
        g.Clear(Parent?.BackColor ?? SystemColors.Control);

        bool conFoco = cajaTexto.Focused;
        Color borde = !Enabled ? Color.FromArgb(222, 226, 230)
                : conFoco ? colorBordeFoco
                : mouseEncima ? Color.FromArgb(134, 142, 150)
                : colorBorde;
        float grosor = conFoco ? 2f : 1.2f;

        var area = new RectangleF(grosor / 2, grosor / 2, Width - grosor - 1, Height - grosor - 1);
        using GraphicsPath ruta = CrearRutaRedondeada(area, radioBorde);
        using (var fondo = new SolidBrush(Enabled ? BackColor : Color.FromArgb(248, 249, 250)))
        {
            g.FillPath(fondo, ruta);
        }
        using var lapiz = new Pen(borde, grosor);
        g.DrawPath(lapiz, ruta);
    }

    // Alt+Abajo y F4 abren la lista, como en un ComboBox de Windows
    private void CajaTexto_KeyDown(KeyEventArgs e)
    {
        if (conLista && (e.KeyCode == Keys.F4 || (e.Alt && e.KeyCode == Keys.Down)))
        {
            e.Handled = e.SuppressKeyPress = true;
            SolicitarLista();
            return;
        }
        OnKeyDown(e);
    }

    private void SolicitarLista()
    {
        if (Enabled)
        {
            cajaTexto.Focus();
            ListaSolicitada?.Invoke(this, EventArgs.Empty);
        }
    }

    private void AlternarContrasenaVisible()
    {
        contrasenaVisible = !contrasenaVisible;
        ActualizarModoContrasena();
        cajaTexto.Focus();
        cajaTexto.SelectionStart = cajaTexto.TextLength;
    }

    private void ActualizarModoContrasena()
    {
        cajaTexto.UseSystemPasswordChar = esContrasena && !contrasenaVisible;
        botonOjo.Visible = esContrasena;
        botonOjo.Text = contrasenaVisible ? GlifoOcultar : GlifoMostrar;
        PerformLayout();
    }

    private void CambiarMouseEncima(bool encima)
    {
        mouseEncima = encima;
        Invalidate();
    }

    private static GraphicsPath CrearRutaRedondeada(RectangleF area, float radio)
    {
        var ruta = new GraphicsPath();
        float diametro = Math.Min(radio * 2, Math.Min(area.Width, area.Height));

        if (diametro <= 0)
        {
            ruta.AddRectangle(area);
            return ruta;
        }

        ruta.AddArc(area.Left, area.Top, diametro, diametro, 180, 90);
        ruta.AddArc(area.Right - diametro, area.Top, diametro, diametro, 270, 90);
        ruta.AddArc(area.Right - diametro, area.Bottom - diametro, diametro, diametro, 0, 90);
        ruta.AddArc(area.Left, area.Bottom - diametro, diametro, diametro, 90, 90);
        ruta.CloseFigure();
        return ruta;
    }
}
