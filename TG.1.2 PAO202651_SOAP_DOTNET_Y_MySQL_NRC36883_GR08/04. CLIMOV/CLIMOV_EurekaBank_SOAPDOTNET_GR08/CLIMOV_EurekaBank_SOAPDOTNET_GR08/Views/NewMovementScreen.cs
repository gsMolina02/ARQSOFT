using CLIMOV_EurekaBank_SOAPDOTNET_GR08.Helpers;
using CLIMOV_EurekaBank_SOAPDOTNET_GR08.Services;
using Microsoft.Maui.Controls.Shapes;
using System.Globalization;

namespace CLIMOV_EurekaBank_SOAPDOTNET_GR08.Views
{
    public sealed class NewMovementScreen : ContentPage
    {
        private readonly EurekaBankClient _client;
        private string _initialAccount;
        private readonly MainFlyoutPage? _flyoutPage;
        private readonly bool _isDirectMenu;

        private readonly Picker _typePicker;
        private readonly Picker _originPicker;
        private readonly Label _originLabel;
        private readonly Picker _destinationPicker;
        private readonly Label _destinationLabel;
        private readonly Entry _amountEntry;
        private readonly Label _amountLabel;
        private readonly Label _messageLabel;
        private readonly ActivityIndicator _loading;
        private readonly Button _submitButton;

        // Tarjeta informativa de cuenta origen/operar
        private readonly Border _originCard;
        private readonly Label _originCardTitle;
        private readonly Label _originOwnerLabel;
        private readonly Label _originCurrencyLabel;
        private readonly Label _originBalanceLabel;

        // Tarjeta informativa de cuenta destino (transferencia)
        private readonly Border _destinationCard;
        private readonly Label _destOwnerLabel;
        private readonly Label _destCurrencyLabel;
        private readonly Label _destBalanceLabel;
        private readonly Label _destNoticeLabel;

        private List<AccountSummary> _allAccounts = new();
        private List<AccountSummary> _destinationAccounts = new();
        private AccountSummary? _selectedOriginAccount;

        public NewMovementScreen(EurekaBankClient client, string account)
            : this(client, account, null, false) { }

        public NewMovementScreen(EurekaBankClient client, string account, MainFlyoutPage? flyoutPage, bool isDirectMenu = false)
        {
            _client = client;
            _initialAccount = account;
            _flyoutPage = flyoutPage;
            _isDirectMenu = isDirectMenu;

            Title = "Nuevo Movimiento";
            BackgroundColor = Color.FromArgb("#F8F9FA");
            NavigationPage.SetHasNavigationBar(this, false);

            _typePicker = new Picker
            {
                Title = "Seleccione tipo de movimiento...",
                TextColor = Color.FromArgb("#212121"),
                TitleColor = Color.FromArgb("#757575"),
                FontSize = 14
            };
            _typePicker.SelectedIndexChanged += OnTypeChanged;

            _originLabel = Label("Cuenta a Operar (Dueño y Moneda):", 13, "#424242", FontAttributes.Bold);

            _originPicker = new Picker
            {
                Title = "Seleccione cuenta, titular y moneda...",
                TextColor = Color.FromArgb("#212121"),
                TitleColor = Color.FromArgb("#757575"),
                FontSize = 13
            };
            _originPicker.SelectedIndexChanged += OnOriginAccountChanged;

            _originCardTitle = new Label { Text = "🏦 DETALLES DE LA CUENTA:", FontSize = 11, FontAttributes = FontAttributes.Bold, TextColor = Color.FromArgb("#1565C0") };
            _originOwnerLabel = new Label { FontSize = 14, FontAttributes = FontAttributes.Bold, TextColor = Color.FromArgb("#212121") };
            _originCurrencyLabel = new Label { FontSize = 13, FontAttributes = FontAttributes.Bold, TextColor = Color.FromArgb("#1565C0") };
            _originBalanceLabel = new Label { FontSize = 13, FontAttributes = FontAttributes.Bold, TextColor = Color.FromArgb("#2E7D32") };

            _originCard = new Border
            {
                BackgroundColor = Colors.White,
                Stroke = Color.FromArgb("#BBDEFB"),
                StrokeThickness = 1,
                Padding = 14,
                StrokeShape = new RoundRectangle { CornerRadius = 12 },
                IsVisible = false,
                Content = new VerticalStackLayout
                {
                    Spacing = 4,
                    Children =
                    {
                        _originCardTitle,
                        _originOwnerLabel,
                        new HorizontalStackLayout
                        {
                            Spacing = 12,
                            Children = { _originCurrencyLabel, _originBalanceLabel }
                        }
                    }
                }
            };

            _destinationLabel = Label("Cuenta Destino (Dueño y Moneda):", 13, "#424242", FontAttributes.Bold);
            _destinationLabel.IsVisible = false;

            _destinationPicker = new Picker
            {
                Title = "Seleccione cuenta destino y moneda...",
                TextColor = Color.FromArgb("#212121"),
                TitleColor = Color.FromArgb("#757575"),
                FontSize = 13,
                IsVisible = false
            };
            _destinationPicker.SelectedIndexChanged += OnDestinationChanged;

            _destOwnerLabel = new Label { FontSize = 14, FontAttributes = FontAttributes.Bold, TextColor = Color.FromArgb("#212121") };
            _destCurrencyLabel = new Label { FontSize = 13, FontAttributes = FontAttributes.Bold, TextColor = Color.FromArgb("#1565C0") };
            _destBalanceLabel = new Label { FontSize = 13, FontAttributes = FontAttributes.Bold, TextColor = Color.FromArgb("#2E7D32") };
            _destNoticeLabel = new Label { FontSize = 12, TextColor = Color.FromArgb("#E65100"), IsVisible = false };

            _destinationCard = new Border
            {
                BackgroundColor = Color.FromArgb("#F1F8E9"),
                Stroke = Color.FromArgb("#C5E1A5"),
                StrokeThickness = 1,
                Padding = 12,
                StrokeShape = new RoundRectangle { CornerRadius = 10 },
                IsVisible = false,
                Content = new VerticalStackLayout
                {
                    Spacing = 3,
                    Children =
                    {
                        new Label { Text = "👤 DATOS DEL DESTINATARIO:", FontSize = 11, FontAttributes = FontAttributes.Bold, TextColor = Color.FromArgb("#2E7D32") },
                        _destOwnerLabel,
                        new HorizontalStackLayout
                        {
                            Spacing = 12,
                            Children = { _destCurrencyLabel, _destBalanceLabel }
                        },
                        _destNoticeLabel
                    }
                }
            };

            _amountLabel = Label("Monto a procesar:", 13, "#424242", FontAttributes.Bold);

            _amountEntry = new Entry
            {
                Placeholder = "0.00",
                Keyboard = Keyboard.Numeric,
                TextColor = Color.FromArgb("#212121"),
                PlaceholderColor = Color.FromArgb("#9E9E9E"),
                FontSize = 16,
                ClearButtonVisibility = ClearButtonVisibility.WhileEditing
            };

            _messageLabel = new Label { FontSize = 14, HorizontalTextAlignment = TextAlignment.Center };
            _loading = new ActivityIndicator { Color = Color.FromArgb("#1565C0"), IsVisible = false };

            _submitButton = new Button
            {
                Text = "Registrar movimiento",
                HeightRequest = 52,
                CornerRadius = 10,
                FontAttributes = FontAttributes.Bold,
                BackgroundColor = Color.FromArgb("#1565C0"),
                TextColor = Colors.White,
                FontSize = 16
            };
            _submitButton.Clicked += OnSubmitClicked;

            Content = new Grid
            {
                RowDefinitions = { new RowDefinition(64), new RowDefinition(GridLength.Star) },
                Children =
                {
                    Header(),
                    new ScrollView
                    {
                        Content = new VerticalStackLayout
                        {
                            Padding = 16,
                            Spacing = 12,
                            Children =
                            {
                                Label("Tipo de Movimiento:", 13, "#424242", FontAttributes.Bold),
                                Field(_typePicker),
                                _originLabel,
                                Field(_originPicker),
                                _originCard,
                                _destinationLabel,
                                Field(_destinationPicker),
                                _destinationCard,
                                _amountLabel,
                                Field(_amountEntry),
                                _submitButton,
                                _loading,
                                _messageLabel
                            }
                        }
                    }.Row(1)
                }
            };
        }

        protected override async void OnAppearing()
        {
            base.OnAppearing();
            await LoadFormAsync();
        }

        private async Task LoadFormAsync()
        {
            SetLoading(true);
            try
            {
                _allAccounts = await _client.GetAccountsAsync();

                var displayItems = _allAccounts
                    .Select(a => $"{a.Codigo} - {a.NombreCliente} [{a.MonedaCompleta}] • Saldo: {a.SimboloMoneda} {a.Saldo:N2}")
                    .ToList();

                _originPicker.ItemsSource = displayItems;

                int initialIdx = _allAccounts.FindIndex(a => a.Codigo.Equals(_initialAccount, StringComparison.OrdinalIgnoreCase));
                if (initialIdx >= 0)
                {
                    _originPicker.SelectedIndex = initialIdx;
                }
                else if (displayItems.Count > 0)
                {
                    _originPicker.SelectedIndex = 0;
                }

                var types = await _client.GetMovementTypesAsync();
                _typePicker.ItemsSource = types;
                if (types.Count > 0)
                {
                    _typePicker.SelectedIndex = 0;
                }

                UpdateDestinationAccountsList();
            }
            catch (Exception ex)
            {
                ShowMessage($"No se pudo cargar el formulario: {ex.Message}", false);
            }
            finally
            {
                SetLoading(false);
            }
        }

        private void OnOriginAccountChanged(object? sender, EventArgs e)
        {
            int idx = _originPicker.SelectedIndex;
            if (idx >= 0 && idx < _allAccounts.Count)
            {
                _selectedOriginAccount = _allAccounts[idx];
                _initialAccount = _selectedOriginAccount.Codigo;

                _originOwnerLabel.Text = $"Titular: {_selectedOriginAccount.NombreCliente}";
                _originCurrencyLabel.Text = $"Moneda: {_selectedOriginAccount.MonedaCompleta}";
                _originBalanceLabel.Text = _selectedOriginAccount.SaldoTexto;
                _originCard.IsVisible = true;

                UpdateAmountLabel();
                UpdateDestinationAccountsList();
            }
            else
            {
                _originCard.IsVisible = false;
            }
        }

        private void UpdateDestinationAccountsList()
        {
            if (_selectedOriginAccount == null) return;

            string selectedOriginCode = _selectedOriginAccount.Codigo;
            _destinationAccounts = _allAccounts
                .Where(a => !a.Codigo.Equals(selectedOriginCode, StringComparison.OrdinalIgnoreCase))
                .ToList();

            var displayDest = _destinationAccounts
                .Select(a => $"{a.Codigo} - {a.NombreCliente} [{a.MonedaCompleta}] • Saldo: {a.SimboloMoneda} {a.Saldo:N2}")
                .ToList();

            _destinationPicker.ItemsSource = displayDest;
            if (displayDest.Count > 0 && _destinationPicker.SelectedIndex < 0)
            {
                _destinationPicker.SelectedIndex = 0;
            }
        }

        private void OnTypeChanged(object? sender, EventArgs e) => UpdateUIVisibility();

        private void UpdateUIVisibility()
        {
            string type = (_typePicker.SelectedItem?.ToString() ?? "DEPOSITO").ToUpperInvariant();
            bool isTransfer = type == "TRANSFERENCIA";

            if (type == "DEPOSITO")
            {
                _originLabel.Text = "Cuenta a Depositar (Dueño y Moneda):";
                _originCardTitle.Text = "🏦 CUENTA A DEPOSITAR:";
                _submitButton.Text = "Realizar Depósito";
            }
            else if (type == "RETIRO")
            {
                _originLabel.Text = "Cuenta de Retiro (Dueño y Moneda):";
                _originCardTitle.Text = "🏦 CUENTA DE RETIRO:";
                _submitButton.Text = "Realizar Retiro";
            }
            else
            {
                _originLabel.Text = "Cuenta Origen (Dueño y Moneda):";
                _originCardTitle.Text = "🏦 CUENTA ORIGEN:";
                _submitButton.Text = "Realizar Transferencia";
            }

            _destinationLabel.IsVisible = isTransfer;
            _destinationPicker.IsVisible = isTransfer;
            _destinationCard.IsVisible = isTransfer && _destinationPicker.SelectedIndex >= 0;

            UpdateAmountLabel();
        }

        private void UpdateAmountLabel()
        {
            string type = (_typePicker.SelectedItem?.ToString() ?? "").ToUpperInvariant();
            if (_selectedOriginAccount != null)
            {
                if (type == "TRANSFERENCIA")
                {
                    _amountLabel.Text = $"Monto a Transferir en {_selectedOriginAccount.NombreMoneda} ({_selectedOriginAccount.SimboloMoneda}):";
                }
                else if (type == "DEPOSITO")
                {
                    _amountLabel.Text = $"Monto a Depositar en {_selectedOriginAccount.NombreMoneda} ({_selectedOriginAccount.SimboloMoneda}):";
                }
                else if (type == "RETIRO")
                {
                    _amountLabel.Text = $"Monto a Retirar en {_selectedOriginAccount.NombreMoneda} ({_selectedOriginAccount.SimboloMoneda}):";
                }
                else
                {
                    _amountLabel.Text = $"Monto en {_selectedOriginAccount.NombreMoneda} ({_selectedOriginAccount.SimboloMoneda}):";
                }
                _amountEntry.Placeholder = $"Monto en {_selectedOriginAccount.NombreMoneda} (ej. 100.00)";
            }
            else
            {
                _amountLabel.Text = "Monto a procesar:";
                _amountEntry.Placeholder = "0.00";
            }
        }

        private void OnDestinationChanged(object? sender, EventArgs e)
        {
            int idx = _destinationPicker.SelectedIndex;
            if (idx < 0 || idx >= _destinationAccounts.Count)
            {
                _destinationCard.IsVisible = false;
                return;
            }

            var dest = _destinationAccounts[idx];
            _destOwnerLabel.Text = $"Dueño: {dest.NombreCliente}";
            _destCurrencyLabel.Text = $"Cuenta {dest.Codigo} • {dest.MonedaCompleta}";
            _destBalanceLabel.Text = dest.SaldoTexto;

            if (_selectedOriginAccount != null && dest.EsSoles != _selectedOriginAccount.EsSoles)
            {
                _destNoticeLabel.Text = $"💱 Conversión: La cuenta destino está en {dest.NombreMoneda}. El débito se efectuará en {_selectedOriginAccount.NombreMoneda} y el banco aplicará la conversión de moneda oficial.";
                _destNoticeLabel.IsVisible = true;
            }
            else
            {
                _destNoticeLabel.IsVisible = false;
            }

            _destinationCard.IsVisible = true;
        }

        private async void OnSubmitClicked(object? sender, EventArgs e)
        {
            var type = _typePicker.SelectedItem?.ToString();
            if (string.IsNullOrWhiteSpace(type))
            {
                ShowMessage("Seleccione un tipo de movimiento.", false);
                return;
            }

            if (_selectedOriginAccount == null)
            {
                ShowMessage("Seleccione una cuenta a operar.", false);
                return;
            }

            if (!decimal.TryParse(_amountEntry.Text, NumberStyles.Number, CultureInfo.CurrentCulture, out var amount) &&
                !decimal.TryParse(_amountEntry.Text, NumberStyles.Number, CultureInfo.InvariantCulture, out amount))
            {
                ShowMessage("Ingrese un monto válido.", false);
                return;
            }

            if (amount <= 0)
            {
                ShowMessage("El monto debe ser mayor a 0.", false);
                return;
            }

            string destination = string.Empty;
            string cuentaOrigen = _selectedOriginAccount.Codigo;

            if (type.Equals("TRANSFERENCIA", StringComparison.OrdinalIgnoreCase))
            {
                int destIdx = _destinationPicker.SelectedIndex;
                if (destIdx < 0 || destIdx >= _destinationAccounts.Count)
                {
                    ShowMessage("Seleccione la cuenta destino.", false);
                    return;
                }
                var destAcc = _destinationAccounts[destIdx];
                destination = destAcc.Codigo;

                bool confirmar = await DisplayAlert(
                    "Confirmar Transferencia",
                    $"¿Deseas transferir {_selectedOriginAccount.SimboloMoneda} {amount:N2} desde tu cuenta {cuentaOrigen} [{_selectedOriginAccount.MonedaCompleta}] a la cuenta {destAcc.Codigo} de {destAcc.NombreCliente} [{destAcc.MonedaCompleta}]?",
                    "Confirmar",
                    "Cancelar");

                if (!confirmar) return;
            }
            else if (type.Equals("RETIRO", StringComparison.OrdinalIgnoreCase))
            {
                if (amount > _selectedOriginAccount.Saldo)
                {
                    ShowMessage($"Saldo insuficiente. Tu saldo es {_selectedOriginAccount.SaldoTexto}.", false);
                    return;
                }

                bool confirmar = await DisplayAlert(
                    "Confirmar Retiro",
                    $"¿Deseas retirar {_selectedOriginAccount.SimboloMoneda} {amount:N2} de la cuenta {cuentaOrigen} ({_selectedOriginAccount.NombreCliente})?",
                    "Confirmar",
                    "Cancelar");

                if (!confirmar) return;
            }
            else if (type.Equals("DEPOSITO", StringComparison.OrdinalIgnoreCase))
            {
                bool confirmar = await DisplayAlert(
                    "Confirmar Depósito",
                    $"¿Deseas depositar {_selectedOriginAccount.SimboloMoneda} {amount:N2} en la cuenta {cuentaOrigen} ({_selectedOriginAccount.NombreCliente})?",
                    "Confirmar",
                    "Cancelar");

                if (!confirmar) return;
            }

            SetLoading(true);
            try
            {
                var result = await _client.ProcessMovementAsync(type, cuentaOrigen, destination, amount);
                if (!result.Success)
                {
                    ShowMessage(result.Mensaje, false);
                    return;
                }

                await DisplayAlert("Éxito", result.Mensaje, "Aceptar");

                if (_isDirectMenu && _flyoutPage != null)
                {
                    _flyoutPage.NavigateTo(new MovementsScreen(_client, cuentaOrigen, _selectedOriginAccount.NombreCliente, true, _flyoutPage));
                }
                else
                {
                    await Navigation.PopAsync();
                }
            }
            catch (Exception ex)
            {
                ShowMessage($"No se pudo registrar: {ex.Message}", false);
            }
            finally
            {
                SetLoading(false);
            }
        }

        private void SetLoading(bool loading)
        {
            _loading.IsRunning = loading;
            _loading.IsVisible = loading;
            _submitButton.IsEnabled = !loading;
        }

        private void ShowMessage(string message, bool success)
        {
            _messageLabel.Text = message;
            _messageLabel.TextColor = success ? Color.FromArgb("#2E7D32") : Color.FromArgb("#C62828");
        }

        private Grid Header()
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
            if (_isDirectMenu)
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
                leftBtn.Clicked += async (_, _) => await Navigation.PopAsync();
            }

            var titleLabel = new Label
            {
                Text = "Nuevo Movimiento",
                TextColor = Colors.White,
                FontSize = 18,
                FontAttributes = FontAttributes.Bold,
                HorizontalTextAlignment = TextAlignment.Center,
                VerticalTextAlignment = TextAlignment.Center
            };

            header.Add(leftBtn, 0, 0);
            header.Add(titleLabel, 1, 0);

            return header;
        }

        private static Border Field(View content) => new()
        {
            Stroke = Color.FromArgb("#E0E0E0"),
            StrokeThickness = 1,
            BackgroundColor = Colors.White,
            Padding = new Thickness(12, 2),
            StrokeShape = new RoundRectangle { CornerRadius = 10 },
            Content = content
        };

        private static Label Label(string text, double size, string color, FontAttributes attributes = FontAttributes.None) => new()
        {
            Text = text,
            FontSize = size,
            TextColor = Color.FromArgb(color),
            FontAttributes = attributes
        };
    }
}
