using CLIMOV_EurekaBank_SOAPDOTNET_GR08.Services;
using Microsoft.Maui.Controls.Shapes;

namespace CLIMOV_EurekaBank_SOAPDOTNET_GR08.Views
{
    public partial class AccountsScreen : ContentPage
    {
        private readonly EurekaBankClient _client;
        private readonly string _user;
        private readonly MainFlyoutPage? _flyoutPage;

        private readonly CollectionView _accountsList;
        private readonly ActivityIndicator _loading;
        private readonly Label _messageLabel;
        private readonly Entry _searchEntry;

        private List<AccountSummary> _allAccounts = new();

        public AccountsScreen(EurekaBankClient client) : this(client, "", null) { }

        public AccountsScreen(EurekaBankClient client, string user) : this(client, user, null) { }

        public AccountsScreen(EurekaBankClient client, string user, MainFlyoutPage? flyoutPage)
        {
            _client = client;
            _user = user ?? "";
            _flyoutPage = flyoutPage;

            Title = "Cuentas de Clientes";
            BackgroundColor = Color.FromArgb("#F8F9FA");
            NavigationPage.SetHasNavigationBar(this, false);

            _searchEntry = new Entry
            {
                Placeholder = "🔍 Buscar por cuenta o cliente...",
                TextColor = Color.FromArgb("#212121"),
                PlaceholderColor = Color.FromArgb("#9E9E9E"),
                FontSize = 14,
                ClearButtonVisibility = ClearButtonVisibility.WhileEditing
            };
            _searchEntry.TextChanged += OnSearchTextChanged;

            var searchCard = new Border
            {
                BackgroundColor = Colors.White,
                Stroke = Color.FromArgb("#E0E0E0"),
                StrokeThickness = 1,
                Margin = new Thickness(16, 12, 16, 6),
                Padding = new Thickness(12, 2),
                StrokeShape = new RoundRectangle { CornerRadius = 10 },
                Content = _searchEntry
            };

            _accountsList = new CollectionView
            {
                SelectionMode = SelectionMode.None,
                Margin = new Thickness(0, 4)
            };

            _accountsList.ItemTemplate = new DataTemplate(() =>
            {
                var codigo = MakeLabel(16, FontAttributes.Bold, "#1565C0");
                codigo.SetBinding(Label.TextProperty, nameof(AccountSummary.Codigo));

                var cliente = MakeLabel(14, FontAttributes.Bold, "#212121");
                cliente.SetBinding(Label.TextProperty, nameof(AccountSummary.ClienteMoneda));

                var saldo = MakeLabel(15, FontAttributes.Bold, "#2E7D32");
                saldo.SetBinding(Label.TextProperty, nameof(AccountSummary.SaldoTexto));

                var estado = MakeLabel(12, FontAttributes.None, "#757575");
                estado.SetBinding(Label.TextProperty, nameof(AccountSummary.EstadoEmail));

                var grid = new Grid
                {
                    ColumnDefinitions =
                    {
                        new ColumnDefinition(GridLength.Star),
                        new ColumnDefinition(GridLength.Auto)
                    }
                };

                grid.Add(new VerticalStackLayout
                {
                    Spacing = 3,
                    Children = { codigo, cliente, saldo, estado }
                }, 0);

                grid.Add(new Label
                {
                    Text = "›",
                    FontSize = 30,
                    TextColor = Color.FromArgb("#BDBDBD"),
                    VerticalOptions = LayoutOptions.Center
                }, 1);

                var tap = new TapGestureRecognizer();
                tap.Tapped += OnAccountTapped;
                grid.GestureRecognizers.Add(tap);

                return new Border
                {
                    BackgroundColor = Colors.White,
                    Stroke = Color.FromArgb("#E0E0E0"),
                    StrokeThickness = 1,
                    Margin = new Thickness(16, 6),
                    Padding = 14,
                    StrokeShape = new RoundRectangle { CornerRadius = 12 },
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
                    searchCard.Row(1),
                    new Grid { Children = { _accountsList, _loading, _messageLabel } }.Row(2)
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
                        Text = "Cuentas de Clientes",
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
            refreshBtn.Clicked += async (s, e) => await LoadAccountsAsync();

            header.Add(menuBtn, 0, 0);
            header.Add(titleStack, 1, 0);
            header.Add(refreshBtn, 2, 0);

            return header;
        }

        private async Task LoadAccountsAsync()
        {
            _loading.IsVisible = true;
            _loading.IsRunning = true;
            _messageLabel.Text = string.Empty;

            try
            {
                _allAccounts = await _client.GetAccountsAsync();
                ApplyFilter();
            }
            catch (Exception ex)
            {
                _accountsList.ItemsSource = null;
                _messageLabel.Text = $"No se pudieron cargar las cuentas: {ex.Message}";
            }
            finally
            {
                _loading.IsRunning = false;
                _loading.IsVisible = false;
            }
        }

        private void OnSearchTextChanged(object? sender, TextChangedEventArgs e)
        {
            ApplyFilter();
        }

        private void ApplyFilter()
        {
            string query = (_searchEntry.Text ?? "").Trim();
            if (string.IsNullOrEmpty(query))
            {
                _accountsList.ItemsSource = _allAccounts;
                _messageLabel.Text = _allAccounts.Count == 0 ? "No hay cuentas disponibles." : string.Empty;
            }
            else
            {
                var filtered = _allAccounts.Where(a =>
                    a.Codigo.Contains(query, StringComparison.OrdinalIgnoreCase) ||
                    a.NombreCliente.Contains(query, StringComparison.OrdinalIgnoreCase)).ToList();

                _accountsList.ItemsSource = filtered;
                _messageLabel.Text = filtered.Count == 0 ? "No se encontraron cuentas con ese criterio." : string.Empty;
            }
        }

        private async void OnAccountTapped(object? sender, TappedEventArgs e)
        {
            if (sender is BindableObject bindable && bindable.BindingContext is AccountSummary account)
            {
                await Navigation.PushAsync(new MovementsScreen(_client, account.Codigo, account.NombreCliente, isMyAccount: false, _flyoutPage));
            }
        }

        private static Label MakeLabel(double size, FontAttributes attributes, string color) => new()
        {
            FontSize = size,
            FontAttributes = attributes,
            TextColor = Color.FromArgb(color)
        };
    }
}
