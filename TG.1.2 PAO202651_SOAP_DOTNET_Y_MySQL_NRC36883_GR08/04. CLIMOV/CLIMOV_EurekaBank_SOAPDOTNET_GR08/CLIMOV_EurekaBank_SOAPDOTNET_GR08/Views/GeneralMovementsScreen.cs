using CLIMOV_EurekaBank_SOAPDOTNET_GR08.Helpers;
using CLIMOV_EurekaBank_SOAPDOTNET_GR08.Services;
using Microsoft.Maui.Controls.Shapes;

namespace CLIMOV_EurekaBank_SOAPDOTNET_GR08.Views
{
    public class GeneralMovementsScreen : ContentPage
    {
        private readonly EurekaBankClient _client;
        private readonly string _user;
        private readonly MainFlyoutPage? _flyoutPage;

        private readonly Picker _accountPicker;
        private readonly CollectionView _movementsList;
        private readonly ActivityIndicator _loading;
        private readonly Label _messageLabel;
        private readonly Label _accountDetailsLabel;
        private readonly Label _balanceLabel;
        private readonly Border _accountInfoCard;
        private readonly Button _newMovementBtn;

        private List<AccountSummary> _accounts = new();
        private string _currentSelectedAccount = "";

        public GeneralMovementsScreen(EurekaBankClient client, string user, MainFlyoutPage? flyoutPage = null)
        {
            _client = client;
            _user = user;
            _flyoutPage = flyoutPage;

            Title = "Movimientos en General";
            BackgroundColor = Color.FromArgb("#F8F9FA");
            NavigationPage.SetHasNavigationBar(this, false);

            _accountPicker = new Picker
            {
                Title = "Seleccione una cuenta...",
                TextColor = Color.FromArgb("#212121"),
                TitleColor = Color.FromArgb("#757575"),
                FontSize = 14
            };
            _accountPicker.SelectedIndexChanged += OnAccountSelected;

            _accountDetailsLabel = new Label
            {
                FontSize = 14,
                FontAttributes = FontAttributes.Bold,
                TextColor = Color.FromArgb("#212121")
            };

            _balanceLabel = new Label
            {
                FontSize = 15,
                FontAttributes = FontAttributes.Bold,
                TextColor = Color.FromArgb("#2E7D32")
            };

            _newMovementBtn = new Button
            {
                Text = "➕ Nuevo Movimiento",
                FontSize = 13,
                FontAttributes = FontAttributes.Bold,
                BackgroundColor = Color.FromArgb("#1565C0"),
                TextColor = Colors.White,
                CornerRadius = 8,
                HeightRequest = 42,
                Margin = new Thickness(0, 6, 0, 0)
            };
            _newMovementBtn.Clicked += OnNewMovementClicked;

            _accountInfoCard = new Border
            {
                BackgroundColor = Colors.White,
                Stroke = Color.FromArgb("#E0E0E0"),
                StrokeThickness = 1,
                Margin = new Thickness(16, 8, 16, 4),
                Padding = 14,
                StrokeShape = new RoundRectangle { CornerRadius = 12 },
                IsVisible = false,
                Content = new VerticalStackLayout
                {
                    Spacing = 4,
                    Children =
                    {
                        _accountDetailsLabel,
                        _balanceLabel,
                        _newMovementBtn
                    }
                }
            };

            _movementsList = new CollectionView
            {
                SelectionMode = SelectionMode.None,
                Margin = new Thickness(0, 4)
            };

            _movementsList.ItemTemplate = new DataTemplate(() =>
            {
                var tipo = new Label { FontSize = 15, FontAttributes = FontAttributes.Bold };
                tipo.SetBinding(Label.TextProperty, nameof(MovementSummary.TipoImporte));
                tipo.SetBinding(Label.TextColorProperty, nameof(MovementSummary.Color));

                var fecha = new Label { FontSize = 12, TextColor = Color.FromArgb("#757575") };
                fecha.SetBinding(Label.TextProperty, nameof(MovementSummary.Fecha));

                var refLabel = new Label { FontSize = 12, TextColor = Color.FromArgb("#424242"), HorizontalTextAlignment = TextAlignment.End };
                refLabel.SetBinding(Label.TextProperty, nameof(MovementSummary.Referencia));

                var saldo = new Label { FontSize = 13, FontAttributes = FontAttributes.Bold, TextColor = Color.FromArgb("#1565C0"), HorizontalTextAlignment = TextAlignment.End };
                saldo.SetBinding(Label.TextProperty, nameof(MovementSummary.SaldoTexto));

                var grid = new Grid
                {
                    Padding = new Thickness(16, 12),
                    RowDefinitions = { new RowDefinition(GridLength.Auto), new RowDefinition(GridLength.Auto) },
                    ColumnDefinitions = { new ColumnDefinition(GridLength.Star), new ColumnDefinition(GridLength.Auto) }
                };
                grid.Add(tipo, 0, 0);
                grid.Add(fecha, 0, 1);
                grid.Add(saldo, 1, 0);
                grid.Add(refLabel, 1, 1);

                return new Border
                {
                    BackgroundColor = Colors.White,
                    Stroke = Color.FromArgb("#EEEEEE"),
                    StrokeThickness = 1,
                    Margin = new Thickness(16, 4),
                    Padding = 0,
                    StrokeShape = new RoundRectangle { CornerRadius = 10 },
                    Content = grid
                };
            });

            _loading = new ActivityIndicator { Color = Color.FromArgb("#1565C0"), IsRunning = false, IsVisible = false };
            _messageLabel = new Label
            {
                HorizontalTextAlignment = TextAlignment.Center,
                TextColor = Color.FromArgb("#757575"),
                FontSize = 14,
                Margin = 20
            };

            var pickerCard = new Border
            {
                BackgroundColor = Colors.White,
                Stroke = Color.FromArgb("#BBDEFB"),
                StrokeThickness = 1,
                Margin = new Thickness(16, 12, 16, 4),
                Padding = new Thickness(14, 8),
                StrokeShape = new RoundRectangle { CornerRadius = 10 },
                Content = new VerticalStackLayout
                {
                    Spacing = 2,
                    Children =
                    {
                        new Label { Text = "SELECCIONE CUENTA DE CLIENTE:", FontSize = 11, FontAttributes = FontAttributes.Bold, TextColor = Color.FromArgb("#1565C0") },
                        _accountPicker
                    }
                }
            };

            Content = new Grid
            {
                RowDefinitions =
                {
                    new RowDefinition(64),
                    new RowDefinition(GridLength.Auto),
                    new RowDefinition(GridLength.Auto),
                    new RowDefinition(GridLength.Star)
                },
                Children =
                {
                    CreateHeader(),
                    pickerCard.Row(1),
                    _accountInfoCard.Row(2),
                    new Grid { Children = { _movementsList, _loading, _messageLabel } }.Row(3)
                }
            };
        }

        protected override async void OnAppearing()
        {
            base.OnAppearing();
            await LoadAccountsAsync();
        }

        private Grid CreateHeader()
        {
            var grid = new Grid
            {
                BackgroundColor = Color.FromArgb("#1565C0"),
                Padding = new Thickness(12, 0),
                ColumnDefinitions =
                {
                    new ColumnDefinition(48),
                    new ColumnDefinition(GridLength.Star),
                    new ColumnDefinition(48)
                }
            };

            var menuBtn = new Button
            {
                Text = "☰",
                BackgroundColor = Colors.Transparent,
                TextColor = Colors.White,
                FontSize = 24,
                WidthRequest = 48
            };
            menuBtn.Clicked += (s, e) =>
            {
                var flyout = _flyoutPage ??
                             (Application.Current?.Windows.Count > 0 ? Application.Current.Windows[0].Page as MainFlyoutPage : null) ??
                             Application.Current?.MainPage as MainFlyoutPage;
                if (flyout != null)
                {
                    flyout.IsPresented = !flyout.IsPresented;
                }
            };

            var titleStack = new HorizontalStackLayout
            {
                Spacing = 8,
                VerticalOptions = LayoutOptions.Center,
                HorizontalOptions = LayoutOptions.Center,
                Children =
                {
                    new Image { Source = "sulli_general.png", WidthRequest = 28, HeightRequest = 28 },
                    new Label
                    {
                        Text = "Movimientos en General",
                        TextColor = Colors.White,
                        FontSize = 17,
                        FontAttributes = FontAttributes.Bold,
                        VerticalTextAlignment = TextAlignment.Center
                    }
                }
            };

            var refreshBtn = new Button
            {
                Text = "↻",
                BackgroundColor = Colors.Transparent,
                TextColor = Colors.White,
                FontSize = 24,
                WidthRequest = 48
            };
            refreshBtn.Clicked += async (s, e) => await LoadAccountsAsync();

            grid.Add(menuBtn, 0, 0);
            grid.Add(titleStack, 1, 0);
            grid.Add(refreshBtn, 2, 0);

            return grid;
        }

        private async Task LoadAccountsAsync()
        {
            _loading.IsVisible = true;
            _loading.IsRunning = true;
            _messageLabel.Text = string.Empty;

            try
            {
                _accounts = await _client.GetAccountsAsync();
                var items = _accounts.Select(a => $"{a.Codigo} - {a.NombreCliente} [{a.MonedaCompleta}] • {a.SaldoTexto}").ToList();
                _accountPicker.ItemsSource = items;

                if (items.Count > 0 && _accountPicker.SelectedIndex < 0)
                {
                    _accountPicker.SelectedIndex = 0;
                }
            }
            catch (Exception ex)
            {
                _messageLabel.Text = $"Error al cargar cuentas: {ex.Message}";
            }
            finally
            {
                _loading.IsRunning = false;
                _loading.IsVisible = false;
            }
        }

        private async void OnAccountSelected(object? sender, EventArgs e)
        {
            int idx = _accountPicker.SelectedIndex;
            if (idx < 0 || idx >= _accounts.Count) return;

            var acc = _accounts[idx];
            _currentSelectedAccount = acc.Codigo;

            _accountDetailsLabel.Text = $"Cuenta {acc.Codigo} • Titular: {acc.NombreCliente}";
            _balanceLabel.Text = $"Moneda: {acc.MonedaCompleta} • {acc.SaldoTexto}";
            _accountInfoCard.IsVisible = true;

            await LoadMovementsForAccountAsync(_currentSelectedAccount);
        }

        private async Task LoadMovementsForAccountAsync(string accountCode)
        {
            _loading.IsVisible = true;
            _loading.IsRunning = true;
            _messageLabel.Text = string.Empty;
            _movementsList.ItemsSource = null;

            try
            {
                var movements = await _client.GetMovementsAsync(accountCode);
                _movementsList.ItemsSource = movements;
                if (movements.Count == 0)
                {
                    _messageLabel.Text = $"La cuenta {accountCode} no registra movimientos.";
                }
            }
            catch (Exception ex)
            {
                _messageLabel.Text = $"No se pudieron cargar los movimientos: {ex.Message}";
            }
            finally
            {
                _loading.IsRunning = false;
                _loading.IsVisible = false;
            }
        }

        private async void OnNewMovementClicked(object? sender, EventArgs e)
        {
            if (string.IsNullOrEmpty(_currentSelectedAccount)) return;
            await Navigation.PushAsync(new NewMovementScreen(_client, _currentSelectedAccount, _flyoutPage));
        }
    }
}

