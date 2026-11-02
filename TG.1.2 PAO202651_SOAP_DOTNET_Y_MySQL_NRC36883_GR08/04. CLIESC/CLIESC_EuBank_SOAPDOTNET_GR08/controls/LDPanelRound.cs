// Archivo: CLIESC_ConUni_SOAPDOTNET_GR08/Controls/LDPanelRound.cs

using System;
using System.ComponentModel;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Windows.Forms;

namespace CLIESC_ConUni_SOAPDOTNET_GR08.Controls
{
    public class LDPanelRound : Panel
    {
        // 1. Campos
        private int cornerRadius = 10;
        private Color borderColor = Color.Violet;
        private int borderThickness = 1;

        // 2. Propiedades Personalizadas
        [Category("LD Code - Appearance")]
        public int CornerRadius
        {
            get { return cornerRadius; }
            set
            {
                if (value >= 1)
                {
                    cornerRadius = value;
                    this.Invalidate(); // Redibujar el control
                }
            }
        }

        [Category("LD Code - Appearance")]
        public Color BorderColor
        {
            get { return borderColor; }
            set
            {
                borderColor = value;
                this.Invalidate(); // Redibujar el control
            }
        }

        [Category("LD Code - Appearance")]
        public int BorderThickness
        {
            get { return borderThickness; }
            set
            {
                if (value >= 0)
                {
                    borderThickness = value;
                    this.Invalidate(); // Redibujar el control
                }
            }
        }

        // El resto de propiedades como BackColor se heredan de Panel.

        // 3. Constructor
        public LDPanelRound()
        {
            // Configuración básica para el dibujo personalizado
            this.DoubleBuffered = true;
        }


        // 4. Método para crear el GraphicsPath redondeado
        private GraphicsPath GetRoundedPath(Rectangle bounds, int radius)
        {
            int diameter = radius * 2;
            Rectangle arc = new Rectangle(bounds.Location, new Size(diameter, diameter));
            GraphicsPath path = new GraphicsPath();

            // Esquina superior izquierda
            path.AddArc(arc, 180, 90);

            // Esquina superior derecha
            arc.X = bounds.Right - diameter;
            path.AddArc(arc, 270, 90);

            // Esquina inferior derecha
            arc.Y = bounds.Bottom - diameter;
            path.AddArc(arc, 0, 90);

            // Esquina inferior izquierda
            arc.X = bounds.Left;
            path.AddArc(arc, 90, 90);

            path.CloseFigure();
            return path;
        }

        // 5. Sobrescribir OnPaint para dibujar la forma y el borde
        protected override void OnPaint(PaintEventArgs e)
        {
            base.OnPaint(e);
            e.Graphics.SmoothingMode = SmoothingMode.AntiAlias;

            Rectangle rect = new Rectangle(0, 0, this.Width, this.Height);

            // Crea la ruta redondeada para la región del panel
            using (GraphicsPath path = GetRoundedPath(rect, cornerRadius))
            {
                // Establecer la región del Panel
                this.Region = new Region(path);

                // Dibujar el borde (si el grosor es > 0)
                if (borderThickness > 0)
                {
                    // Usar un rectángulo ligeramente más pequeño para dibujar el borde
                    // y centrar el lápiz para que no se corte por la región
                    Rectangle borderRect = new Rectangle(rect.X, rect.Y, rect.Width - 1, rect.Height - 1);

                    using (GraphicsPath borderPath = GetRoundedPath(borderRect, cornerRadius))
                    using (Pen pen = new Pen(borderColor, borderThickness))
                    {
                        pen.Alignment = PenAlignment.Center;
                        e.Graphics.DrawPath(pen, borderPath);
                    }
                }
            }
        }

        // 6. Sobrescribir OnResize para redibujar al cambiar el tamaño
        protected override void OnResize(EventArgs eventargs)
        {
            base.OnResize(eventargs);
            this.Invalidate(); // Asegurar que el borde se recalcule al cambiar de tamaño
        }
    }
}