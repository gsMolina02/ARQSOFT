using CLIMOV_EurekaBank_SOAPDOTNET_GR08.Helpers;
using CLIMOV_EurekaBank_SOAPDOTNET_GR08.Services;
using Microsoft.Maui.Controls.Shapes;

namespace CLIMOV_EurekaBank_SOAPDOTNET_GR08.Views
{
    public partial class MovementsScreen : ContentPage
    {
        private readonly EurekaBankClient _client;
        private string _account;
        private readonly string _titular;
        private readonly bool _isMyAccount;
        private readonly MainFlyoutPage? _flyoutPage;
        private readonly List<string> _myAccounts;

        private readonly CollectionView _movementsList;
        private readonly ActivityIndicator _loading;
        private readonly Label _messageLabel;

        // Elementos de la tarjeta superior
        private readonly Label _accountTitleLabel;
        private readonly Label _accountSubtitleLabel;
        private readonly Label _accountBalanceLabel;
        private readonly Picker _accountSwitchPicker;
        private readonly Border _topCard;

        private List<AccountSummary> _loadedAccounts = new();
        private AccountSummary? _currentAccountData;

        public MovementsScreen(EurekaBankClient client, string account)
            : this(client, account, "", false, null, null) { }

        public MovementsScreen(EurekaBankClient client, string account, string titular, bool isMyAccount, MainFlyoutPage? flyoutPage, List<string>? myAccounts = null)
        {
            _client = client;
            _account = account;
            _titular = titular;
            _isMyAccount = isMyAccount;
            _flyoutPage = flyoutPage;
            _myAccounts = myAccounts ?? (_isMyAccount ? new List<string> { _account } : new List<string>());

            Title = _isMyAccount ? "Mis Movimientos" : $"Movimientos - {_account}";
            BackgroundColor = Color.FromArgb("#F8F9FA");
            NavigationPage.SetHasNavigationBar(this, false);

            _accountTitleLabel = new Label
            {
                FontSize = 16,
                FontAttributes = FontAttributes.Bold,
                TextColor = Color.FromArgb("#1565C0")
            };

            _accountSubtitleLabel = new Label
            {
                FontSize = 13,
                TextColor = Color.FromArgb("#424242")
            };

            _accountBalanceLabel = new Label
            {
                FontSize = 14,
                FontAttributes = FontAttributes.Bold,
                TextColor = Color.FromArgb("#2E7D32")
            };

            _accountSwitchPicker = new Picker
            {
                Title = "Cambiar de cuenta...",
                FontSize = 13,
                TextColor = Color.FromArgb("#1565C0"),
                TitleColor = Color.FromArgb("#757575"),
                IsVisible = _isMyAccount && _myAccounts.Count > 1
            };
            _accountSwitchPicker.SelectedIndexChanged += OnAccountSwitchChanged;

            _topCard = new Border
            {
                BackgroundColor = Colors.White,
                Stroke = Color.FromArgb("#BBDEFB"),
                StrokeThickness = 1,
                Margin = new Thickness(16, 10, 16, 4),
                Padding = 14,
                StrokeShape = new RoundRectangle { CornerRadius = 12 },
                Content = new VerticalStackLayout
                {
                    Spacing = 4,
                    Children =
                    {
                        new HorizontalStackLayout
                        {
                            Spacing = 6,
                            Children =
                            {
                                new Label { Text = _isMyAccount ? "⭐" : "🏦", FontSize = 14 },
                                new Label
                                {
                                    Text = _isMyAccount ? "MI CUENTA BANCARIA:" : "DETALLES DE LA CUENTA:",
                                    FontSize = 11,
                                    FontAttributes = FontAttributes.Bold,
                                    TextColor = Color.FromArgb("#1565C0")
                                }
                            }
                        },
                        _accountTitleLabel,
                        _accountSubtitleLabel,
                        _accountBalanceLabel,
                        _accountSwitchPicker
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

                var refLabel = new Label
                {
                    FontSize = 12,
                    TextColor = Color.FromArgb("#616161"),
                    HorizontalTextAlignment = TextAlignment.End
                };
                refLabel.SetBinding(Label.TextProperty, nameof(MovementSummary.Referencia));

                var saldo = new Label
                {
                    FontSize = 13,
                    FontAttributes = FontAttributes.Bold,
                    TextColor = Color.FromArgb("#1565C0"),
                    HorizontalTextAlignment = TextAlignment.End
                };
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

            var fab = new Button
            {
                Text = "+",
                WidthRequest = 56,
                HeightRequest = 56,
                CornerRadius = 28,
                BackgroundColor = Color.FromArgb("#1565C0"),
                TextColor = Colors.White,
                FontSize = 28,
                FontAttributes = FontAttributes.Bold,
                HorizontalOptions = LayoutOptions.End,
                VerticalOptions = LayoutOptions.End,
                Margin = new Thickness(0, 0, 20, 24)
            };
            fab.Clicked += OnNewMovementClicked;

            Content = new Grid
            {
                RowDefinitions =
                {
                    new RowDefinition(64),
                    new RowDefinition(GridLength.Auto),
                    new RowDefinition(GridLength.Star)
                },
                Children =
                {
                    CreateHeader(),
                    _topCard.Row(1),
                    new Grid { Children = { _movementsList, _loading, _messageLabel, fab } }.Row(2)
                }
            };
        }

        protected override async void OnAppearing()
        {
            base.OnAppearing();
            await RefreshDataAsync();
        }

        private Grid CreateHeader()
        {
            var header = new Grid
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

            Button leftBtn;
            if (_isMyAccount)
            {
                leftBtn = new Button
                {
                    Text = "☰",
                    BackgroundColor = Colors.Transparent,
                    TextColor = Colors.White,
                    FontSize = 24,
                    WidthRequest = 48
                };
                leftBtn.Clicked += (s, e) =>
                {
                    var flyout = _flyoutPage ??
                                 (Application.Current?.Windows.Count > 0 ? Application.Current.Windows[0].Page as MainFlyoutPage : null) ??
                                 Application.Current?.MainPage as MainFlyoutPage;
                    if (flyout != null)
                    {
                        flyout.IsPresented = !flyout.IsPresented;
                    }
                };
            }
            else
            {
                leftBtn = new Button
                {
                    Text = "‹",
                    BackgroundColor = Colors.Transparent,
                    TextColor = Colors.White,
                    FontSize = 32,
                    WidthRequest = 48
                };
                leftBtn.Clicked += async (s, e) => await Navigation.PopAsync();
            }

            var titleText = _isMyAccount ? "Mis Movimientos" : $"Movimientos";
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
                        Text = titleText,
                        TextColor = Colors.White,
                        FontSize = 18,
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
            refreshBtn.Clicked += async (s, e) => await RefreshDataAsync();

            header.Add(leftBtn, 0, 0);
            header.Add(titleStack, 1, 0);
            header.Add(refreshBtn, 2, 0);

            return header;
        }

        private async Task RefreshDataAsync()
        {
            _loading.IsVisible = true;
            _loading.IsRunning = true;
            _messageLabel.Text = string.Empty;

            try
            {
                _loadedAccounts = await _client.GetAccountsAsync();

                if (_isMyAccount && _myAccounts.Count > 0)
                {
                    var pickerItems = new List<string>();
                    foreach (var accCode in _myAccounts)
                    {
                        var found = _loadedAccounts.FirstOrDefault(a => a.Codigo.Equals(accCode, StringComparison.OrdinalIgnoreCase));
                        if (found != null)
                        {
                            pickerItems.Add($"💳 {found.Codigo} - {found.MonedaCompleta} ({found.SaldoTexto})");
                        }
                        else
                        {
                            pickerItems.Add($"💳 {accCode}");
                        }
                    }
                    _accountSwitchPicker.ItemsSource = pickerItems;

                    int selectedIdx = _myAccounts.FindIndex(c => c.Equals(_account, StringComparison.OrdinalIgnoreCase));
                    if (selectedIdx >= 0 && selectedIdx < pickerItems.Count)
                    {
                        _accountSwitchPicker.SelectedIndex = selectedIdx;
                    }
                    else if (pickerItems.Count > 0)
                    {
                        _accountSwitchPicker.SelectedIndex = 0;
                    }
                }

                UpdateCardView();
                await LoadMovementsAsync();
            }
            catch (Exception ex)
            {
                _messageLabel.Text = $"Error al cargar datos: {ex.Message}";
            }
            finally
            {
                _loading.IsRunning = false;
                _loading.IsVisible = false;
            }
        }

        private void UpdateCardView()
        {
            _currentAccountData = _loadedAccounts.FirstOrDefault(a => a.Codigo.Equals(_account, StringComparison.OrdinalIgnoreCase));

            if (_currentAccountData != null)
            {
                _accountTitleLabel.Text = $"Cuenta {_currentAccountData.Codigo} • {_currentAccountData.MonedaCompleta}";
                _accountSubtitleLabel.Text = $"Titular: {_currentAccountData.NombreCliente}";
                _accountBalanceLabel.Text = $"{_currentAccountData.SaldoTexto}";
            }
            else
            {
                _accountTitleLabel.Text = $"Cuenta {_account}";
                _accountSubtitleLabel.Text = !string.IsNullOrEmpty(_titular) ? $"Titular: {_titular}" : "";
                _accountBalanceLabel.Text = "";
            }
        }

        private async void OnAccountSwitchChanged(object? sender, EventArgs e)
        {
            int idx = _accountSwitchPicker.SelectedIndex;
            if (idx >= 0 && idx < _myAccounts.Count)
            {
                _account = _myAccounts[idx];
                UpdateCardView();
                await LoadMovementsAsync();
            }
        }

        private async Task LoadMovementsAsync()
        {
            _movementsList.ItemsSource = null;
            _messageLabel.Text = string.Empty;

            try
            {
                var movements = await _client.GetMovementsAsync(_account);
                _movementsList.ItemsSource = movements;
                if (movements.Count == 0)
                {
                    _messageLabel.Text = $"La cuenta {_account} no registra movimientos.";
                }
            }
            catch (Exception ex)
            {
                _messageLabel.Text = $"No se pudieron cargar los movimientos: {ex.Message}";
            }
        }

        private async void OnNewMovementClicked(object? sender, EventArgs e)
        {
            await Navigation.PushAsync(new NewMovementScreen(_client, _account, _flyoutPage));
        }
    }
}
