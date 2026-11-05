using System.ComponentModel;
using System.Drawing.Drawing2D;

namespace ec.edu.monster.Vista.Controles;

/// <summary>
/// Muestra una imagen cubriendo todo el control (como "object-fit: cover"):
/// la agranda hasta llenar el ancho y el alto sin deformarla y recorta lo que sobra.
/// El punto de enfoque indica qué parte de la imagen se conserva al recortar.
/// </summary>
public class ImagenRecortada : Control
{
    private Image? imagen;
    private float focoHorizontal = 0.5f;
    private float focoVertical = 0.5f;

    public ImagenRecortada()
    {
        SetStyle(ControlStyles.UserPaint | ControlStyles.AllPaintingInWmPaint
                | ControlStyles.OptimizedDoubleBuffer | ControlStyles.ResizeRedraw, true);
        TabStop = false;
    }

    [Category("Imagen")]
    [Description("Imagen que se muestra recortada para cubrir todo el control.")]
    [DefaultValue(null)]
    public Image? Imagen
    {
        get => imagen;
        set { imagen = value; Invalidate(); }
    }

    [Category("Imagen")]
    [Description("Parte de la imagen que se conserva al recortar los costados: 0 = izquierda, 0.5 = centro, 1 = derecha.")]
    [DefaultValue(0.5f)]
    public float FocoHorizontal
    {
        get => focoHorizontal;
        set { focoHorizontal = Math.Clamp(value, 0f, 1f); Invalidate(); }
    }

    [Category("Imagen")]
    [Description("Parte de la imagen que se conserva al recortar arriba y abajo: 0 = arriba, 0.5 = centro, 1 = abajo.")]
    [DefaultValue(0.5f)]
    public float FocoVertical
    {
        get => focoVertical;
        set { focoVertical = Math.Clamp(value, 0f, 1f); Invalidate(); }
    }

    protected override void OnPaint(PaintEventArgs e)
    {
        Graphics g = e.Graphics;
        g.Clear(BackColor);

        if (imagen is null || Width <= 0 || Height <= 0)
        {
            return;
        }

        // Escala mínima que cubre el ancho y el alto; el sobrante se recorta según el foco
        float escala = Math.Max((float)Width / imagen.Width, (float)Height / imagen.Height);
        float anchoVisible = Width / escala;
        float altoVisible = Height / escala;
        float x = (imagen.Width - anchoVisible) * focoHorizontal;
        float y = (imagen.Height - altoVisible) * focoVertical;

        g.InterpolationMode = InterpolationMode.HighQualityBicubic;
        g.PixelOffsetMode = PixelOffsetMode.HighQuality;
        g.DrawImage(imagen, new Rectangle(0, 0, Width, Height),
                new RectangleF(x, y, anchoVisible, altoVisible), GraphicsUnit.Pixel);
    }
}
