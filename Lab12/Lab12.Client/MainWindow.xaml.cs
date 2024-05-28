using System.ComponentModel;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Text.Json;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using Lab12.Contracts;

namespace Lab12.Client;

/// <summary>
/// Interaction logic for MainWindow.xaml
/// </summary>
public partial class MainWindow : Window
{
    private TcpClient? _client = null;
    private bool stop = false;
    
    private Task? _checkConnectionTask = null;
    
    public MainWindow()
    {
        InitializeComponent();
        _checkConnectionTask = Task.Run(async () =>
        {
            while (!stop)
            {
                if (_client is not null && _client.GetStream().DataAvailable)
                {
                    _client = null;
                    Status.Dispatcher.Invoke(() =>
                    {
                        Status.Text = "Disconnected";
                    });
                    DisconnectButton.Dispatcher.Invoke(() =>
                    {
                        DisconnectButton.IsEnabled = false;
                    });
                    ConnectButton.Dispatcher.Invoke(() =>
                    {
                        ConnectButton.IsEnabled = true;
                    });
                    SendButton.Dispatcher.Invoke(() =>
                    {
                        SendButton.IsEnabled = false;
                    });
                }

                await Task.Delay(1000);
            }
        });
    }

    private void ConnectButton_OnClick(object sender, RoutedEventArgs e)
    {
        _client?.Close();

        try
        {
            _client = new TcpClient(IPAddress.Loopback.ToString(), 1234);
        }
        catch (SocketException ex)
        {
            Console.WriteLine($"Failed to connect to server:{ex.Message}");
            Status.Text = "Failed to connect!";
            SendButton.IsEnabled = false;
            MessageBox.Show($"Failed to connect to server:{ex.Message}");
            return;
        }

        Status.Text = "Connected";
        SendButton.IsEnabled = true;
        DisconnectButton.IsEnabled = true;
        ConnectButton.IsEnabled = false;
    }

    private async void SendButton_OnClick(object sender, RoutedEventArgs e)
    {
        if (_client is null)
        {
            MessageBox.Show("Connect to the server first");
            return;
        }

        if (_client.GetStream().DataAvailable)
        {
            _client?.Close();
            _client = null;
            Status.Text = "Disconnected";
            DisconnectButton.IsEnabled = false;
            ConnectButton.IsEnabled = true;
            SendButton.IsEnabled = false;
            return;
        }
        
        var data = GetData();
        
        NumberATextBox.IsEnabled = false;
        NumberBTextBox.IsEnabled = false;
        ContentTextBox.IsEnabled = false;
        ConnectButton.IsEnabled = false;
        SendButton.IsEnabled = false;
        DisconnectButton.IsEnabled = false;
        Status.Text = "Sending...";

        var result = await Task.Run(async () =>
        {
            var streamWriter = new StreamWriter(_client.GetStream());
            await streamWriter.WriteLineAsync(JsonSerializer.Serialize(data));
            await streamWriter.FlushAsync();

            var streamReader = new StreamReader(_client.GetStream());
            var newData = JsonSerializer.Deserialize<Data>((await streamReader.ReadLineAsync())!);
            return newData;
        });
        
        if (result is not null)
        {
            NumberATextBox.Text = result!.NumberA.ToString();
            NumberBTextBox.Text = result.NumberB.ToString();
            ContentTextBox.Text = result.Content;
            ResultTextBox.Text = result.Result!.ToString()!;
        }
        
        NumberATextBox.IsEnabled = true;
        NumberBTextBox.IsEnabled = true;
        ContentTextBox.IsEnabled = true;
        SendButton.IsEnabled = true;
        DisconnectButton.IsEnabled = true;

        Status.Text = "Done";
    }

    private Data GetData() => new Data
    {
        NumberA = int.Parse(NumberATextBox.Text),
        NumberB = int.Parse(NumberBTextBox.Text),
        Content = ContentTextBox.Text
    };

    private void DisconnectButton_OnClick(object sender, RoutedEventArgs e)
    {
        _client?.Close();
        _client = null;
        Status.Text = "Disconnected";
        DisconnectButton.IsEnabled = false;
        ConnectButton.IsEnabled = true;
        SendButton.IsEnabled = false;
    }

    private void MainWindow_OnClosed(object? sender, EventArgs e)
    {
        stop = true;
        _checkConnectionTask.Wait();
        _client?.Close();
    }
}