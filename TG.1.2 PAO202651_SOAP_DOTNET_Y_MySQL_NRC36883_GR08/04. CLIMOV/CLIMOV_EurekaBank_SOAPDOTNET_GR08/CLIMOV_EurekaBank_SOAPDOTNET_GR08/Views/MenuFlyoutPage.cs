using CLIMOV_EurekaBank_SOAPDOTNET_GR08.Helpers;
using CLIMOV_EurekaBank_SOAPDOTNET_GR08.Services;
using Microsoft.Maui.Controls.Shapes;

namespace CLIMOV_EurekaBank_SOAPDOTNET_GR08.Views
{
    public class MenuFlyoutPage : ContentPage
    {
        private readonly EurekaBankClient _client;
        private readonly string _user;
        private readonly MainFlyoutPage _flyoutPage;
        private readonly string _titular;
        private readonly List<string> _cuentasAsignadas;
        private readonly string _rol;

        public MenuFlyoutPage(EurekaBankClient client, string user, MainFlyoutPage flyoutPage)
        {
            _client = client;
            _user = user;
            _flyoutPage = flyoutPage;

            var info = UserInfoHelper.GetInfo(_user);
            _titular = info.Titular;
            _cuentasAsignadas = info.CuentasAsignadas;
            _rol = info.Rol;

            Title = "Menú Principal";
            BackgroundColor = Color.FromArgb("#F8F9FA");
            NavigationPage.SetHasNavigationBar(this, false);

            Content = BuildContent();
        }

        private View BuildContent()
        {
            var header = CreateHeader();
            var menuList = CreateMenuList();
            var footer = CreateFooter();

            return new Grid
            {
                RowDefinitions =
                {
                    new RowDefinition(GridLength.Auto),
                    new RowDefinition(GridLength.Star),
                    new RowDefinition(GridLength.Auto)
                },
                Children =
                {
                    header.Row(0),
                    new ScrollView { Content = menuList }.Row(1),
                    footer.Row(2)
                }
            };
        }

        private View CreateHeader()
        {
            string cuentasTexto = string.Join(" • ", _cuentasAsignadas);

            return new Border
            {
                BackgroundColor = Color.FromArgb("#1565C0"),
                StrokeThickness = 0,
                Padding = new Thickness(20, 48, 20, 20),
                Content = new VerticalStackLayout
                {
                    Spacing = 8,
                    Children =
                    {
                        new HorizontalStackLayout
                        {
                            Spacing = 12,
                            VerticalOptions = LayoutOptions.Center,
                            Children =
                            {
                                new Border
                                {
                                    BackgroundColor = Colors.White,
                                    StrokeThickness = 0,
                                    WidthRequest = 56,
                                    HeightRequest = 56,
                                    StrokeShape = new RoundRectangle { CornerRadius = 28 },
                                    Padding = 6,
                                    Content = new Image
                                    {
                                        Source = "sulli_general.png",
                                        Aspect = Aspect.AspectFit
                                    }
                                },
                                new VerticalStackLayout
                                {
                                    VerticalOptions = LayoutOptions.Center,
                                    Spacing = 2,
                                    Children =
                                    {
                                        new Label
                                        {
                                            Text = "EUREKA BANK",
                                            FontSize = 18,
                                            FontAttributes = FontAttributes.Bold,
                                            TextColor = Colors.White
                                        },
                                        new Label
                                        {
                                            Text = "Banca Móvil",
                                            FontSize = 12,
                                            TextColor = Color.FromArgb("#BBDEFB")
                                        }
                                    }
                                }
                            }
                        },
                        new BoxView { HeightRequest = 1, Color = Color.FromArgb("#42A5F5"), Margin = new Thickness(0, 4) },
                        new Label
                        {
                            Text = _titular,
                            FontSize = 15,
                            FontAttributes = FontAttributes.Bold,
                            TextColor = Colors.White
                        },
                        new HorizontalStackLayout
                        {
                            Spacing = 6,
                            Children =
                            {
                                new Label
                                {
                                    Text = $"@{_user}",
                                    FontSize = 13,
                                    FontAttributes = FontAttributes.Italic,
                                    TextColor = Color.FromArgb("#E3F2FD")
                                },
                                new Label
                                {
                                    Text = "•",
                                    FontSize = 13,
                                    TextColor = Color.FromArgb("#BBDEFB")
                                },
                                new Label
                                {
                                    Text = _rol,
                                    FontSize = 12,
                                    TextColor = Color.FromArgb("#E3F2FD")
                                }
                            }
                        },
                        new Border
                        {
                            BackgroundColor = Color.FromArgb("#0D47A1"),
                            StrokeThickness = 0,
                            Padding = new Thickness(10, 5),
                            StrokeShape = new RoundRectangle { CornerRadius = 6 },
                            HorizontalOptions = LayoutOptions.Start,
                            Content = new Label
                            {
                                Text = $"💳 Cuentas: {cuentasTexto}",
                                FontSize = 12,
                                FontAttributes = FontAttributes.Bold,
                                TextColor = Colors.White
                            }
                        }
                    }
                }
            };
        }

        private View CreateMenuList()
        {
            var stack = new VerticalStackLayout
            {
                Padding = new Thickness(12, 16),
                Spacing = 10
            };

            string primeraCuenta = _cuentasAsignadas.Count > 0 ? _cuentasAsignadas[0] : "00300001";
            string infoCuentas = string.Join(" y ", _cuentasAsignadas);

            stack.Children.Add(CreateMenuItem(
                "👥",
                "Cuentas de Clientes",
                "Saldos y lista de clientes",
                () => _flyoutPage.NavigateTo(new AccountsScreen(_client, _user, _flyoutPage))));

            stack.Children.Add(CreateMenuItem(
                "⭐",
                "Mis Movimientos",
                $"Ver cuentas: {infoCuentas}",
                () => _flyoutPage.NavigateTo(new MovementsScreen(_client, primeraCuenta, _titular, isMyAccount: true, _flyoutPage, _cuentasAsignadas))));

            stack.Children.Add(CreateMenuItem(
                "📊",
                "Movimientos en General",
                "Consultar por cuenta bancaria",
                () => _flyoutPage.NavigateTo(new GeneralMovementsScreen(_client, _user, _flyoutPage))));

            stack.Children.Add(CreateMenuItem(
                "💸",
                "Nuevo Movimiento",
                "Depósito, Retiro o Transferencia",
                () => _flyoutPage.NavigateTo(new NewMovementScreen(_client, primeraCuenta, _flyoutPage, isDirectMenu: true))));

            return stack;
        }

        private Border CreateMenuItem(string icon, string title, string subtitle, Action onClick)
        {
            var itemGrid = new Grid
            {
                ColumnDefinitions =
                {
                    new ColumnDefinition(44),
                    new ColumnDefinition(GridLength.Star),
                    new ColumnDefinition(24)
                },
                VerticalOptions = LayoutOptions.Center
            };

            var iconBorder = new Border
            {
                BackgroundColor = Color.FromArgb("#E3F2FD"),
                StrokeThickness = 0,
                WidthRequest = 40,
                HeightRequest = 40,
                StrokeShape = new RoundRectangle { CornerRadius = 10 },
                Content = new Label
                {
                    Text = icon,
                    FontSize = 20,
                    HorizontalOptions = LayoutOptions.Center,
                    VerticalOptions = LayoutOptions.Center
                }
            };

            var textStack = new VerticalStackLayout
            {
                Spacing = 2,
                VerticalOptions = LayoutOptions.Center,
                Children =
                {
                    new Label
                    {
                        Text = title,
                        FontSize = 14,
                        FontAttributes = FontAttributes.Bold,
                        TextColor = Color.FromArgb("#212121")
                    },
                    new Label
                    {
                        Text = subtitle,
                        FontSize = 11,
                        TextColor = Color.FromArgb("#757575")
                    }
                }
            };

            var arrow = new Label
            {
                Text = "›",
                FontSize = 22,
                TextColor = Color.FromArgb("#9E9E9E"),
                HorizontalOptions = LayoutOptions.Center,
                VerticalOptions = LayoutOptions.Center
            };

            itemGrid.Add(iconBorder, 0, 0);
            itemGrid.Add(textStack, 1, 0);
            itemGrid.Add(arrow, 2, 0);

            var border = new Border
            {
                BackgroundColor = Colors.White,
                Stroke = Color.FromArgb("#E0E0E0"),
                StrokeThickness = 1,
                Padding = new Thickness(12, 10),
                StrokeShape = new RoundRectangle { CornerRadius = 12 },
                Content = itemGrid
            };

            var tap = new TapGestureRecognizer();
            tap.Tapped += (s, e) => onClick();
            border.GestureRecognizers.Add(tap);

            return border;
        }

        private View CreateFooter()
        {
            var footerGrid = new Grid
            {
                ColumnDefinitions =
                {
                    new ColumnDefinition(36),
                    new ColumnDefinition(GridLength.Star),
                    new ColumnDefinition(24)
                }
            };

            var iconLabel = new Label { Text = "🚪", FontSize = 20, VerticalOptions = LayoutOptions.Center };
            var textStack = new VerticalStackLayout
            {
                Spacing = 1,
                VerticalOptions = LayoutOptions.Center,
                Children =
                {
                    new Label { Text = "Cerrar Sesión", FontSize = 14, FontAttributes = FontAttributes.Bold, TextColor = Color.FromArgb("#C62828") },
                    new Label { Text = "Salir de la aplicación", FontSize = 11, TextColor = Color.FromArgb("#E57373") }
                }
            };
            var chevron = new Label { Text = "›", FontSize = 22, TextColor = Color.FromArgb("#C62828"), VerticalOptions = LayoutOptions.Center };

            footerGrid.Add(iconLabel, 0, 0);
            footerGrid.Add(textStack, 1, 0);
            footerGrid.Add(chevron, 2, 0);

            var logoutBorder = new Border
            {
                BackgroundColor = Color.FromArgb("#FFEBEE"),
                Stroke = Color.FromArgb("#FFCDD2"),
                StrokeThickness = 1,
                Margin = new Thickness(12, 8, 12, 24),
                Padding = new Thickness(14, 12),
                StrokeShape = new RoundRectangle { CornerRadius = 12 },
                Content = footerGrid
            };

            var tap = new TapGestureRecognizer();
            tap.Tapped += async (s, e) =>
            {
                bool salir = await DisplayAlert("Cerrar Sesión", "¿Deseas cerrar tu sesión de Eureka Bank?", "Sí, salir", "Cancelar");
                if (salir)
                {
                    _flyoutPage.Logout();
                }
            };
            logoutBorder.GestureRecognizers.Add(tap);

            return logoutBorder;
        }
    }
}
