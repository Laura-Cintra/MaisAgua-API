package br.com.fiap.mais_agua.service;

import br.com.fiap.mais_agua.model.Dispositivo;
import br.com.fiap.mais_agua.model.LeituraDispositivo;
import br.com.fiap.mais_agua.repository.DispositivoRepository;
import br.com.fiap.mais_agua.repository.LeituraDispositivoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class LeituraDispositivoService {

    @Autowired
    private LeituraDispositivoRepository leituraRepository;

    @Autowired
    private DispositivoRepository dispositivoRepository;

    Random random = new Random();

    /**
     * Gera automaticamente leituras de todos os dispositivos cadastrados a cada 3 dias às 9h da manhã
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void gerarLeituraAutomatica() {
        log.info("Iniciando geração automática de leitura dos dispositivos...");

        List<Dispositivo> dispositivos = dispositivoRepository.findAll();

        for (Dispositivo dispositivo : dispositivos) {
            LeituraDispositivo leitura = new LeituraDispositivo();
            leitura.setDispositivo(dispositivo);
            leitura.setDataHora(LocalDateTime.now());

            // Gera nível percentual entre 10% e 100%
            int nivelPct = random.nextInt(91) + 10;
            leitura.setNivelPct(nivelPct);

            // Gera turbidez entre 0 e 100 NTU (exemplo — você pode ajustar)
            int turbidez = random.nextInt(101);
            leitura.setTurbidezNtu(turbidez);

            // Gera pH entre 5.00 e 14.00 (água não potável)
            double ph = 5 + (14 - 5) * random.nextDouble();
            leitura.setPh_int(BigDecimal.valueOf(ph).setScale(2, RoundingMode.HALF_UP));

            leituraRepository.save(leitura);

            log.info("Leitura gerada para dispositivo ID {} - Nível: {}%, Turbidez: {} NTU, pH: {}",
                    dispositivo.getId_dispositivo(), nivelPct, turbidez, leitura.getPh_int());
        }

        log.info("Geração de leitura concluída.");
    }
}
