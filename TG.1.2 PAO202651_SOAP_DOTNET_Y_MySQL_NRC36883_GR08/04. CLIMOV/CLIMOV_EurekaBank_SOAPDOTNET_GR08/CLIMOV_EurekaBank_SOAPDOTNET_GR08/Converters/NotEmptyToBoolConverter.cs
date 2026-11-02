using System;
using System.Globalization;
using Microsoft.Maui.Controls;

namespace CLIMOV_EurekaBank_SOAPDOTNET_GR08.Converters
{
    public class NotEmptyToBoolConverter : IValueConverter
    {
        public object? Convert(object? value, Type targetType, object? parameter, CultureInfo culture)
        {
            if (value is string s)
                return !string.IsNullOrEmpty(s);
            return value != null;
        }

        public object? ConvertBack(object? value, Type targetType, object? parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }
}
