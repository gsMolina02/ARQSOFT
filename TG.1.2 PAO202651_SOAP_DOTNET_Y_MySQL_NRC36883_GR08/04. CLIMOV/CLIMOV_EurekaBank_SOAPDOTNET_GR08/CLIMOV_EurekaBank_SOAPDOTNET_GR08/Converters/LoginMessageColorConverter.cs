using System;
using System.Globalization;
using Microsoft.Maui.Controls;

namespace CLIMOV_EurekaBank_SOAPDOTNET_GR08.Converters
{
    public class LoginMessageColorConverter : IValueConverter
    {
        public object? Convert(object? value, Type targetType, object? parameter, CultureInfo culture)
        {
            return Colors.Gray;
        }

        public object? ConvertBack(object? value, Type targetType, object? parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }
}
