using System.IO;
using System.Text.RegularExpressions;
using System.Windows;
using MessageBox = System.Windows.MessageBox;

namespace Lab08;

public partial class CreateFileWindow : Window
{
    private readonly DirectoryInfo _path;
    public CreateFileWindow(DirectoryInfo path)
    {
        _path = path;
        
        InitializeComponent();
    }

    private void CreateButton_Click(object sender, RoutedEventArgs e)
    {
        var name = NameTextBox.Text;
        var isFile = FileType.IsChecked;
        var attributes = FileAttributes.Normal
                         | (IsReadOnly.IsChecked == true ? FileAttributes.ReadOnly : FileAttributes.Normal)
                         | (IsArchive.IsChecked == true ? FileAttributes.Archive : FileAttributes.Normal)
                         | (IsHidden.IsChecked == true ? FileAttributes.Hidden : FileAttributes.Normal)
                         | (IsSystem.IsChecked == true ? FileAttributes.System : FileAttributes.Normal);

        if (isFile == true && !Regex.IsMatch(name, @"^[a-zA-Z0-9_~-]{1,8}\.(txt|php|html)$"))
        {
            MessageBox.Show("Invalid file name", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
        }

        if (isFile == false)
        {
            Directory.CreateDirectory(Path.Combine(_path.FullName, name));
        }
        else
        {
            File.Create(Path.Combine(_path.FullName, name)).Close();
            File.SetAttributes(Path.Combine(_path.FullName, name), attributes);
        }
        Close();
    }

    private void CancelButton_Click(object sender, RoutedEventArgs e)
    {
        Close();
    }
}