using System.Net;
using System.Net.Sockets;
using System.Text.Json;
using Lab12.Contracts;

namespace Lab12.Server;

public class Server
{
    private readonly Dictionary<int, TcpClient> _clients = new();
    private readonly List<Task> _tasks = new();
    
    public async Task Start(CancellationToken cancellationToken)
    {
        var listener = new TcpListener(IPAddress.Any, 1234);
        listener.Start();
        Console.WriteLine("Server listening on tcp:0.0.0.0:1234");

        try
        {
            while (!cancellationToken.IsCancellationRequested)
            {
                var client = await listener.AcceptTcpClientAsync(cancellationToken);
                if (cancellationToken.IsCancellationRequested)
                {
                    break;
                }

                var port = (client.Client.RemoteEndPoint as IPEndPoint)?.Port;
                if (port is null)
                {
                    continue;
                }

                if (!_clients.TryAdd(port.Value, client))
                {
                    Console.WriteLine($"[ERR] Port {port.Value} is already taken");
                    continue;
                }

                _tasks.Add(Task.Run(() => HandleClient(client, port.Value, cancellationToken), cancellationToken));
            }
        }
        catch (OperationCanceledException)
        {
            // Ignore
        }

        await Task.WhenAll(_tasks);
    }

    private async Task HandleClient(TcpClient client, int port, CancellationToken cancellationToken)
    {
        Console.WriteLine($"[{port}] Connected");
        var writer = new StreamWriter(client.GetStream());
        var reader = new StreamReader(client.GetStream());

        try
        {
            while (!cancellationToken.IsCancellationRequested)
            {
                try
                {
                    var message = await reader.ReadLineAsync();
                    if (message is null)
                    {
                        await writer.WriteLineAsync($"[{port}] Empty line");
                        await writer.FlushAsync();
                        continue;
                    }

                    var data = JsonSerializer.Deserialize<Data>(message);
                    if (data is null)
                    {
                        continue;
                    }

                    Console.WriteLine($"[{port}] Received: {message}");

                    data = await HandleMessage(data, cancellationToken);
                    
                    Console.WriteLine($"[{port} Processed: Sending back results");

                    await writer.WriteLineAsync(JsonSerializer.Serialize(data));
                    await writer.FlushAsync();
                }
                catch (OperationCanceledException ex)
                {
                    throw ex;
                }
                catch (IOException)
                {
                    break;
                }
                catch (Exception ex)
                {
                    Console.WriteLine($"[{port}] Error: {ex.Message}");
                    break;
                }
            }
        }
        catch (OperationCanceledException)
        {
            await writer.WriteLineAsync();
            await writer.FlushAsync();
        }
        
        client.Close();
        Console.WriteLine($"[{port}] Disconnected");
        _clients.Remove(port);
    }

    private static async Task<Data> HandleMessage(Data data, CancellationToken cancellationToken)
    {
        await Task.Delay(Random.Shared.Next(500, 3500), cancellationToken);
        return new Data
        {
            Result = data.NumberA + data.NumberB,
            NumberA = data.NumberA,
            NumberB = data.NumberB,
            Content = data.Content.ToUpper()
        };
    }
}