namespace Microsoft.Maui.Controls
{
    public static class MyGridExtensions
    {
        public static T Row<T>(this T view, int row) where T : View
        {
            Grid.SetRow(view, row);
            return view;
        }

        public static T Col<T>(this T view, int col) where T : View
        {
            Grid.SetColumn(view, col);
            return view;
        }

        public static T RowSpan<T>(this T view, int span) where T : View
        {
            Grid.SetRowSpan(view, span);
            return view;
        }

        public static T ColSpan<T>(this T view, int span) where T : View
        {
            Grid.SetColumnSpan(view, span);
            return view;
        }
    }
}
