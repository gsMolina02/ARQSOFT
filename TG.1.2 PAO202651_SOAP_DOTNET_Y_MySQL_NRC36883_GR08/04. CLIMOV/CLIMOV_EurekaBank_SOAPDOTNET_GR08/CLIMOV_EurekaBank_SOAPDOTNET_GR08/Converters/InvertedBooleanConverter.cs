using System;
using System.Globalization;
using Microsoft.Maui.Controls;

namespace CLIMOV_EurekaBank_SOAPDOTNET_GR08.Converters
{
    public class InvertedBooleanConverter : IValueConverter
    {
        public object? Convert(object? value, Type targetType, object? parameter, CultureInfo culture)
        {
            if (value is bool b)
                return !b;
            return false;
        }

        public object? ConvertBack(object? value, Type targetType, object? parameter, CultureInfo culture)
        {
            if (value is bool b)
                return !b;
            return false;
        }
    }
}
