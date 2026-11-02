using System;
using System.Globalization;
using Microsoft.Maui.Controls;

namespace CLIMOV_EurekaBank_SOAPDOTNET_GR08.Converters
{
    public class BooleanToOpacityConverter : IValueConverter
    {
        public object? Convert(object? value, Type targetType, object? parameter, CultureInfo culture)
        {
            if (value is bool b)
                return b ? 1.0 : 0.5;
            return 0.5;
        }

        public object? ConvertBack(object? value, Type targetType, object? parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }
}
