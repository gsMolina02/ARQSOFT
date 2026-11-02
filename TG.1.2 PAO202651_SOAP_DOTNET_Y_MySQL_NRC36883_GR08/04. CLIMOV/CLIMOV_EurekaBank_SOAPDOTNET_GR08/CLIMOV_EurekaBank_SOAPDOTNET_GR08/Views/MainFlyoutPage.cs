using CLIMOV_EurekaBank_SOAPDOTNET_GR08.Services;

namespace CLIMOV_EurekaBank_SOAPDOTNET_GR08.Views
{
    public class MainFlyoutPage : FlyoutPage
    {
        private readonly EurekaBankClient _client;
        private readonly string _user;

        public MainFlyoutPage(EurekaBankClient client, string user)
        {
            _client = client;
            _user = user;

            FlyoutLayoutBehavior = FlyoutLayoutBehavior.Popover;
            Flyout = new MenuFlyoutPage(client, user, this);
            Detail = new NavigationPage(new AccountsScreen(client, user, this))
            {
                BarBackgroundColor = Color.FromArgb("#1565C0"),
                BarTextColor = Colors.White
            };
            NavigationPage.SetHasNavigationBar(Detail, false);
        }

        public void NavigateTo(Page page)
        {
            Detail = new NavigationPage(page)
            {
                BarBackgroundColor = Color.FromArgb("#1565C0"),
                BarTextColor = Colors.White
            };
            NavigationPage.SetHasNavigationBar(page, false);
            IsPresented = false;
        }

        public void Logout()
        {
            var loginPage = new NavigationPage(new LoginScreen())
            {
                BarBackgroundColor = Color.FromArgb("#1565C0"),
                BarTextColor = Colors.White
            };
            NavigationPage.SetHasNavigationBar(loginPage, false);

            if (Application.Current != null)
            {
                if (Application.Current.Windows.Count > 0)
                    Application.Current.Windows[0].Page = loginPage;
                else
                    Application.Current.MainPage = loginPage;
            }
        }
    }
}

